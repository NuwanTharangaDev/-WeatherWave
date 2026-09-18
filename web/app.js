/**
 * WeatherWave Web Dashboard Logic
 * Powered by OpenWeatherMap API
 */

const API_KEY = "Your_API_Key";
const BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
const FORECAST_URL = "https://api.openweathermap.org/data/2.5/forecast";
const UV_URL = "https://api.openweathermap.org/data/2.5/uvi";
const ICON_BASE = "https://openweathermap.org/img/wn/";

// Popular Cities Database for Autocomplete Suggestions
const POPULAR_CITIES = [
    "Homagama, LK", "Colombo, LK", "Kandy, LK", "Galle, LK",
    "London, UK", "New York, US", "Tokyo, JP", "Paris, FR",
    "Sydney, AU", "Dubai, AE", "Singapore, SG", "Toronto, CA",
    "Berlin, DE", "Mumbai, IN", "Seoul, KR", "Los Angeles, US",
    "Bangkok, TH", "Rome, IT", "Madrid, ES", "Chicago, US"
];

// State Management
let currentCity = "Homagama";
let currentUnit = "metric"; // 'metric' (°C) or 'imperial' (°F)
let weatherCache = null;
let forecastCache = null;
let searchHistory = JSON.parse(localStorage.getItem("weatherwave_history") || "[\"Homagama\", \"London\", \"Tokyo\"]");

// DOM Elements
const searchInput = document.getElementById("searchInput");
const searchBtn = document.getElementById("searchBtn");
const locationBtn = document.getElementById("locationBtn");
const searchSuggestions = document.getElementById("searchSuggestions");
const recentSearchesBar = document.getElementById("recentSearchesBar");
const recentPills = document.getElementById("recentPills");
const statusBadge = document.getElementById("statusBadge");

const unitCelsiusBtn = document.getElementById("unitCelsius");
const unitFahrenheitBtn = document.getElementById("unitFahrenheit");

const cityNameEl = document.getElementById("cityName");
const currentDateTimeEl = document.getElementById("currentDateTime");
const mainTempEl = document.getElementById("mainTemp");
const weatherDescEl = document.getElementById("weatherDesc");
const feelsLikeEl = document.getElementById("feelsLike");
const mainWeatherIconEl = document.getElementById("mainWeatherIcon");

const detailFeelsLike = document.getElementById("detailFeelsLike");
const detailWind = document.getElementById("detailWind");
const detailHumidity = document.getElementById("detailHumidity");
const detailVisibility = document.getElementById("detailVisibility");
const detailUV = document.getElementById("detailUV");

const statPressure = document.getElementById("statPressure");
const statDewPoint = document.getElementById("statDewPoint");
const sunriseTimeEl = document.getElementById("sunriseTime");
const sunsetTimeEl = document.getElementById("sunsetTime");

const hourlyContainer = document.getElementById("hourlyContainer");
const forecastContainer = document.getElementById("forecastContainer");

// Initialize Event Listeners & Initial Fetch
document.addEventListener("DOMContentLoaded", () => {
    renderRecentSearches();
    fetchWeatherData(currentCity);

    searchBtn.addEventListener("click", handleSearch);
    searchInput.addEventListener("keypress", (e) => {
        if (e.key === "Enter") handleSearch();
    });

    searchInput.addEventListener("input", handleAutocomplete);
    document.addEventListener("click", (e) => {
        if (!searchInput.contains(e.target) && !searchSuggestions.contains(e.target)) {
            searchSuggestions.classList.add("hidden");
        }
    });

    if (locationBtn) {
        locationBtn.addEventListener("click", handleCurrentLocation);
    }

    unitCelsiusBtn.addEventListener("click", () => switchUnit("metric"));
    unitFahrenheitBtn.addEventListener("click", () => switchUnit("imperial"));
});

function handleSearch() {
    const city = searchInput.value.trim();
    if (city) {
        currentCity = city;
        saveRecentSearch(city);
        fetchWeatherData(currentCity);
        searchSuggestions.classList.add("hidden");
    }
}

function handleAutocomplete() {
    const query = searchInput.value.trim().toLowerCase();
    if (!query) {
        searchSuggestions.classList.add("hidden");
        return;
    }

    const matches = POPULAR_CITIES.filter(c => c.toLowerCase().includes(query)).slice(0, 5);
    if (matches.length === 0) {
        searchSuggestions.classList.add("hidden");
        return;
    }

    searchSuggestions.innerHTML = matches.map(c => `<div class="suggestion-item"><i class="fa-solid fa-location-dot"></i> ${c}</div>`).join("");
    searchSuggestions.classList.remove("hidden");

    searchSuggestions.querySelectorAll(".suggestion-item").forEach(item => {
        item.addEventListener("click", () => {
            const cityName = item.textContent.trim();
            searchInput.value = cityName;
            handleSearch();
        });
    });
}

function handleCurrentLocation() {
    if (!navigator.geolocation) {
        updateStatus("Geolocation not supported by browser", true);
        return;
    }

    updateStatus("Detecting current location...", false);
    navigator.geolocation.getCurrentPosition(
        (position) => {
            const { latitude, longitude } = position.coords;
            fetchWeatherDataByCoords(latitude, longitude);
        },
        (error) => {
            updateStatus("Location permission denied or unavailable", true);
        },
        { timeout: 10000 }
    );
}

function saveRecentSearch(city) {
    const cleanCity = city.split(",")[0].trim();
    searchHistory = searchHistory.filter(c => c.toLowerCase() !== cleanCity.toLowerCase());
    searchHistory.unshift(cleanCity);
    searchHistory = searchHistory.slice(0, 5);
    localStorage.setItem("weatherwave_history", JSON.stringify(searchHistory));
    renderRecentSearches();
}

function renderRecentSearches() {
    if (!recentPills || !recentSearchesBar) return;
    if (searchHistory.length === 0) {
        recentSearchesBar.classList.add("hidden");
        return;
    }

    recentSearchesBar.classList.remove("hidden");
    recentPills.innerHTML = searchHistory.map(c => `<button class="recent-pill">${c}</button>`).join("");
    recentPills.querySelectorAll(".recent-pill").forEach(pill => {
        pill.addEventListener("click", () => {
            currentCity = pill.textContent;
            searchInput.value = currentCity;
            fetchWeatherData(currentCity);
        });
    });
}

function switchUnit(unit) {
    if (currentUnit === unit) return;
    currentUnit = unit;

    unitCelsiusBtn.classList.toggle("active", unit === "metric");
    unitFahrenheitBtn.classList.toggle("active", unit === "imperial");

    if (weatherCache && forecastCache) {
        renderCurrentWeather(weatherCache);
        renderForecast(forecastCache);
    } else {
        fetchWeatherData(currentCity);
    }
}

async function fetchWeatherData(city) {
    updateStatus("Updating weather data...", false);
    try {
        const weatherUrl = `${BASE_URL}?q=${encodeURIComponent(city)}&appid=${API_KEY}&units=${currentUnit}`;
        const forecastUrl = `${FORECAST_URL}?q=${encodeURIComponent(city)}&appid=${API_KEY}&units=${currentUnit}`;

        const [weatherRes, forecastRes] = await Promise.all([
            fetch(weatherUrl),
            fetch(forecastUrl)
        ]);

        if (!weatherRes.ok) {
            if (weatherRes.status === 404) throw new Error("City not found");
            throw new Error(`API Error: ${weatherRes.status}`);
        }

        weatherCache = await weatherRes.json();
        forecastCache = await forecastRes.json();

        renderCurrentWeather(weatherCache);
        renderForecast(forecastCache);

        if (weatherCache.coord) {
            fetchUVIndex(weatherCache.coord.lat, weatherCache.coord.lon);
        }

        updateStatus("Live API Data", false);
    } catch (error) {
        console.error("Error fetching weather:", error);
        updateStatus(`Error: ${error.message}`, true);
    }
}

async function fetchWeatherDataByCoords(lat, lon) {
    updateStatus("Fetching weather for your location...", false);
    try {
        const weatherUrl = `${BASE_URL}?lat=${lat}&lon=${lon}&appid=${API_KEY}&units=${currentUnit}`;
        const forecastUrl = `${FORECAST_URL}?lat=${lat}&lon=${lon}&appid=${API_KEY}&units=${currentUnit}`;

        const [weatherRes, forecastRes] = await Promise.all([
            fetch(weatherUrl),
            fetch(forecastUrl)
        ]);

        if (!weatherRes.ok) throw new Error(`API Error: ${weatherRes.status}`);

        weatherCache = await weatherRes.json();
        forecastCache = await forecastRes.json();

        currentCity = weatherCache.name;
        searchInput.value = currentCity;
        saveRecentSearch(currentCity);

        renderCurrentWeather(weatherCache);
        renderForecast(forecastCache);
        fetchUVIndex(lat, lon);

        updateStatus("Live Location Data", false);
    } catch (error) {
        console.error("Error fetching location weather:", error);
        updateStatus(`Location Error: ${error.message}`, true);
    }
}

async function fetchUVIndex(lat, lon) {
    try {
        const res = await fetch(`${UV_URL}?lat=${lat}&lon=${lon}&appid=${API_KEY}`);
        if (res.ok) {
            const data = await res.json();
            const uvVal = data.value;
            const rating = getUVRating(uvVal);
            detailUV.textContent = `${uvVal.toFixed(1)} (${rating})`;
        } else {
            detailUV.textContent = "N/A";
        }
    } catch (e) {
        detailUV.textContent = "N/A";
    }
}

function getUVRating(val) {
    if (val <= 2) return "Low";
    if (val <= 5) return "Moderate";
    if (val <= 7) return "High";
    if (val <= 10) return "Very High";
    return "Extreme";
}

function renderCurrentWeather(data) {
    const { name, sys, main, weather, wind, visibility, dt } = data;
    const unitSymbol = currentUnit === "metric" ? "°C" : "°F";
    const speedUnit = currentUnit === "metric" ? "km/h" : "mph";

    cityNameEl.textContent = `${name}, ${sys.country}`;
    currentDateTimeEl.textContent = formatDateTime(dt);

    mainTempEl.textContent = `${Math.round(main.temp)}°`;
    weatherDescEl.textContent = capitalizeWords(weather[0].description);
    feelsLikeEl.textContent = `Feels like ${Math.round(main.feels_like)}${unitSymbol}`;
    mainWeatherIconEl.src = `${ICON_BASE}${weather[0].icon}@4x.png`;

    detailFeelsLike.textContent = `${Math.round(main.feels_like)}${unitSymbol}`;
    
    const windSpeedVal = currentUnit === "metric" ? Math.round(wind.speed * 3.6) : Math.round(wind.speed);
    detailWind.textContent = `${windSpeedVal} ${speedUnit}`;

    detailHumidity.textContent = `${main.humidity}%`;
    detailVisibility.textContent = visibility ? `${(visibility / 1000).toFixed(1)} km` : "N/A";

    statPressure.textContent = `${main.pressure} hPa`;
    
    const dewPointVal = calculateDewPoint(main.temp, main.humidity);
    statDewPoint.textContent = `${dewPointVal.toFixed(1)}${unitSymbol}`;

    sunriseTimeEl.textContent = formatTime(sys.sunrise);
    sunsetTimeEl.textContent = formatTime(sys.sunset);

    applyTheme(weather[0].main);
}

function renderForecast(data) {
    const list = data.list;
    const unitSymbol = "°";

    // 1. Hourly Forecast Timeline (First 8 entries)
    hourlyContainer.innerHTML = "";
    const hourlyItems = list.slice(0, 8);

    hourlyItems.forEach(item => {
        const timeStr = formatHourlyTime(item.dt_txt);
        const iconCode = item.weather[0].icon;
        const temp = Math.round(item.main.temp);

        const card = document.createElement("div");
        card.className = "hourly-card";
        card.innerHTML = `
            <span class="hourly-time">${timeStr}</span>
            <div class="hourly-icon"><img src="${ICON_BASE}${iconCode}.png" alt="icon"></div>
            <span class="hourly-temp">${temp}${unitSymbol}</span>
        `;
        hourlyContainer.appendChild(card);
    });

    // 2. 5-Day Forecast Calculation (Group by date)
    forecastContainer.innerHTML = "";
    const dailyMap = {};

    list.forEach(item => {
        const dateKey = item.dt_txt.split(" ")[0];
        if (!dailyMap[dateKey]) {
            dailyMap[dateKey] = [];
        }
        dailyMap[dateKey].push(item);
    });

    const todayKey = new Date().toISOString().split("T")[0];
    const keys = Object.keys(dailyMap);
    let targetKeys = keys;
    if (dailyMap[todayKey] && keys.length > 5) {
        targetKeys = keys.filter(k => k !== todayKey);
    }
    targetKeys = targetKeys.slice(0, 5);

    targetKeys.forEach(dateKey => {
        const dayItems = dailyMap[dateKey];
        let maxTemp = -Infinity;
        let minTemp = Infinity;
        let representativeItem = dayItems[0];
        let minDiff = 24;

        dayItems.forEach(item => {
            if (item.main.temp_max > maxTemp) maxTemp = item.main.temp_max;
            if (item.main.temp_min < minTemp) minTemp = item.main.temp_min;

            const hour = parseInt(item.dt_txt.split(" ")[1].split(":")[0], 10);
            const diff = Math.abs(hour - 12);
            if (diff < minDiff) {
                minDiff = diff;
                representativeItem = item;
            }
        });

        const dayName = getDayName(representativeItem.dt);
        const iconCode = representativeItem.weather[0].icon;
        const desc = capitalizeWords(representativeItem.weather[0].description);

        const row = document.createElement("div");
        row.className = "forecast-item";
        row.innerHTML = `
            <span class="forecast-day">${dayName}</span>
            <div class="forecast-info">
                <img src="${ICON_BASE}${iconCode}.png" alt="icon">
                <span class="forecast-desc">${desc}</span>
            </div>
            <span class="forecast-temp">${Math.round(maxTemp)}° / ${Math.round(minTemp)}°</span>
        `;
        forecastContainer.appendChild(row);
    });
}

// Dynamic Weather Background Theme Handler
function applyTheme(condition) {
    document.body.classList.remove("theme-sunny", "theme-cloudy", "theme-rainy", "theme-snowy", "theme-thunder");
    const cond = condition ? condition.toLowerCase() : "";

    if (cond.includes("clear") || cond.includes("sun")) {
        document.body.classList.add("theme-sunny");
    } else if (cond.includes("rain") || cond.includes("drizzle")) {
        document.body.classList.add("theme-rainy");
    } else if (cond.includes("snow")) {
        document.body.classList.add("theme-snowy");
    } else if (cond.includes("thunder") || cond.includes("storm")) {
        document.body.classList.add("theme-thunder");
    } else {
        document.body.classList.add("theme-cloudy");
    }
}

// Helper Utilities
function formatDateTime(timestamp) {
    const date = new Date(timestamp * 1000);
    const options = { weekday: 'long', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
    return date.toLocaleDateString('en-US', options);
}

function formatTime(timestamp) {
    const date = new Date(timestamp * 1000);
    return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
}

function formatHourlyTime(dtTxtStr) {
    const date = new Date(dtTxtStr.replace(/-/g, "/"));
    return date.toLocaleTimeString('en-US', { hour: 'numeric', hour12: true });
}

function getDayName(timestamp) {
    const date = new Date(timestamp * 1000);
    return date.toLocaleDateString('en-US', { weekday: 'short' });
}

function calculateDewPoint(temp, humidity) {
    const a = 17.27;
    const b = 237.7;
    const alpha = ((a * temp) / (b + temp)) + Math.log(humidity / 100.0);
    return (b * alpha) / (a - alpha);
}

function capitalizeWords(str) {
    if (!str) return "";
    return str.replace(/\b\w/g, c => c.toUpperCase());
}

function updateStatus(message, isError) {
    statusBadge.innerHTML = `<span class="status-dot" style="background-color: ${isError ? '#ff5252' : '#ff9f43'}"></span> ${message}`;
    statusBadge.style.color = isError ? '#ff5252' : '#ff9f43';
}
