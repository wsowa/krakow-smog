var map;
var sensors = {}

var openInfoWindow = null;


var markerImageGood
var markerImageBad
var markerImageNoData

function initMap() {
    alertify.set('notifier','position', 'top-center');

    markerImageGood = new google.maps.MarkerImage("http://www.googlemapsmarkers.com/v1/009900/");
    markerImageBad = new google.maps.MarkerImage("http://www.googlemapsmarkers.com/v1/990000/");
    markerImageNoData = new google.maps.MarkerImage("http://www.googlemapsmarkers.com/v1/999999/");

    fetchSensors();
    console.log(sensors);
    map = new google.maps.Map(document.getElementById('map'), {
      zoom: 10,
      center: {lat: 50.0337307, lng: 19.9997478}
    });
    google.maps.event.addListener(map, "click", (e) => closeInfoWindow());
}

function fetchSensors() {
    fetch("https://krakow-smog.appspot.com/sensors") // Call the fetch function passing the url of the API as a parameter
    .then((res) => {if (!res.ok) { throw Error(res.statusText); } return res;})
    .then((res) => res.json())
    .then((res) => res.forEach(s => placeMarker(s)))
    .catch((res) => {
        alertify.error('Filed to fetch sensors locations', 0);
        console.log(res)
    });
}

function placeMarker(sensor) {
    sensors[sensor.sensorId] = sensor
    var m = new google.maps.Marker({
        position: {lat: sensor.latitude, lng: sensor.longitude},
        map: map,
        icon: markerImageNoData
    })
    sensor.marker = m

    var infowindow = new google.maps.InfoWindow({
        content: getInfoWindowContent(sensor)
    });

    google.maps.event.addListener(m, 'click', function() {
        closeInfoWindow()
        openInfoWindow = infowindow
        infowindow.open(map,m);
    });
}

function closeInfoWindow() {
    if (openInfoWindow) openInfoWindow.close();
}


function getInfoWindowContent(sensor) {
    address = []
    if(sensor.locality) address.push(sensor.locality)
    if(sensor.street) address.push(sensor.street)
    if(sensor.streetNumber) address.push(sensor.streetNumber)
    return '<div id="content">'+
           '<div class="sensorId">Sensor nr ' + sensor.sensorId + '</div>'+
           '<div class="sensorAddress">' + address.join(", ") + '</div>'+
           '</div>'
}

function updateMarkers(startDate, endDate, hours, maxExceededHours, maxLimitExceed, maxExceededDaysPercentage) {
    console.log(startDate, endDate, hours[0], hours[1], maxExceededHours, maxLimitExceed, maxExceededDaysPercentage);
    var payload = {
        minDate : startDate,
        maxDate : endDate,
        minHour : hours[0],
        maxHour : hours[1],
        maxExceededDaysPercentage : maxExceededDaysPercentage,
        maxLimitExceed : maxLimitExceed,
        maxExceededHours : maxExceededHours
    }
    fetch("https://krakow-smog.appspot.com/sensors/matchCriteria", {
            headers: {
              'Accept': 'application/json',
              'Content-Type': 'application/json'
            },
            method: "POST",
            body: JSON.stringify(payload)
    }).
    then((res) => {if (!res.ok) { throw Error(res.statusText); } return res;})
    .then((res) => res.json())
    .then(function (res) {
        $('#submit-btn').text('Update')
        $('#submit-btn').attr("disabled", false);
        Object.keys(res).forEach(s => updateMarkerIcon(s, res[s]))
    })
    .catch((res) => {
        alertify.error('Filed to fetch sensors state', 0);
        console.log(res)
            $('#submit-btn').text('Update')
            $('#submit-btn').attr("disabled", false);
    });

}

function updateMarkerIcon(sensor, state) {
    if (state == "GOOD") sensors[sensor].marker.setIcon(markerImageGood)
    else if (state == "BAD") sensors[sensor].marker.setIcon(markerImageBad)
    else if (state == "INSUFFICIENT_DATA") sensors[sensor].marker.setIcon(markerImageNoData)
    else {
        sensors[sensor].marker.setIcon(markerImageNoData)
        console.log(sensor, state)
    }
}