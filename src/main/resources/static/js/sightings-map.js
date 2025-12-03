/**
 * UFO Sightings Interactive Map
 * Handles map initialization and display for sighting locations
 */

let currentMap = null;

/**
 * Initialize the map with sighting location data
 */
function initializeMap() {
    const mapElement = document.getElementById('map');
    if (!mapElement) {
        console.error('Map element not found');
        return;
    }
    
    // Check if Leaflet is loaded
    if (typeof L === 'undefined') {
        console.error('Leaflet library not loaded');
        mapElement.innerHTML = '<div style="padding: 20px; text-align: center; color: #666;">Map loading... Please refresh if map doesn\'t appear.</div>';
        return;
    }
    
    // If map already exists, remove it first
    if (currentMap) {
        try {
            currentMap.remove();
        } catch(e) {
            console.warn('Error removing existing map:', e);
        }
        currentMap = null;
    }
    
    const lat = parseFloat(mapElement.dataset.lat);
    const lng = parseFloat(mapElement.dataset.lng);
    const location = mapElement.dataset.location;
    
    if (isNaN(lat) || isNaN(lng)) {
        console.error('Invalid coordinates:', lat, lng);
        mapElement.innerHTML = '<div style="padding: 20px; text-align: center; color: #666;">Invalid location data</div>';
        return;
    }
    
    try {
        // Clear loading message
        const loadingDiv = mapElement.querySelector('.map-loading');
        if (loadingDiv) {
            loadingDiv.style.display = 'none';
        }
        
        // Create new map instance
        currentMap = L.map('map', {
            center: [lat, lng],
            zoom: 13,
            zoomControl: true,
            attributionControl: true
        });
        
        // Add OpenStreetMap tiles
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
            maxZoom: 18
        }).addTo(currentMap);
        
        // Custom UFO icon
        const ufoIcon = L.divIcon({
            html: '🛸',
            iconSize: [30, 30],
            iconAnchor: [15, 15],
            popupAnchor: [0, -15],
            className: 'ufo-marker'
        });
        
        // Add marker for the sighting location
        const marker = L.marker([lat, lng], {icon: ufoIcon}).addTo(currentMap);
        
        // Add popup with sighting information
        marker.bindPopup(`
            <div style="text-align: center; font-size: 14px;">
                <strong>🛸 UFO Sighting</strong><br/>
                <em>${location}</em><br/>
                <small>Coordinates: ${lat.toFixed(6)}, ${lng.toFixed(6)}</small>
            </div>
        `).openPopup();
        
        console.log('Map initialized successfully for coordinates:', lat, lng);
        
        // Force map to resize after initialization
        setTimeout(function() {
            if (currentMap) {
                currentMap.invalidateSize();
            }
        }, 100);
        
    } catch (error) {
        console.error('Error initializing map:', error);
        mapElement.innerHTML = '<div style="padding: 20px; text-align: center; color: #666;">Error loading map. Please refresh the page.</div>';
        if (currentMap) {
            try {
                currentMap.remove();
            } catch(e) {
                // Ignore cleanup errors
            }
            currentMap = null;
        }
    }
}

/**
 * Clean up map resources
 */
function cleanupMap() {
    if (currentMap) {
        try {
            currentMap.remove();
        } catch(e) {
            console.warn('Error cleaning up map:', e);
        }
        currentMap = null;
    }
}

/**
 * Initialize map when DOM is ready
 */
function initMapOnReady() {
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initializeMap);
    } else {
        // DOM is already ready
        initializeMap();
    }
}

/**
 * Set up event listeners for map functionality
 */
function setupMapEventListeners() {
    // Clean up on page unload
    window.addEventListener('beforeunload', cleanupMap);
    
    // Initialize map
    initMapOnReady();
}

// Auto-initialize when script loads
if (document.getElementById('map')) {
    setupMapEventListeners();
}