let mapContainer = document.getElementById('map');
let mapOption = {
    center: new kakao.maps.LatLng(37, 127),   // 지도의 중심 좌표
    level: 12    // 지도 확대 레벨
};

// 지도 생성
let map = new kakao.maps.Map(mapContainer, mapOption);
getCurrentLocation();

let message = '';
// 정보창 생성
let infowindow = new kakao.maps.InfoWindow({
    content: message,
    removable: true 
});

let overlay = new kakao.maps.CustomOverlay({
    map: map,
    yAnchor: 1,
    zIndex: 1
});

// 마커 클러스터 생성
let clusterer = new kakao.maps.MarkerClusterer({
    map: map,
    averageCener: true,  // 클러스터의 위치를 장소들의 좌표의 평균 좌표로 설정한다.
    minClusterSize: 10,  // 10 미만의 레벨에서는 클러스터링을 하지 않는다.
    disableClickZoom: true  // 클러스터를 클릭했을 떄 지도가 확대되지 않도록 한다.
});

// 주소-좌표를 변환할 객체를 생성한다.
let geocoder = new kakao.maps.services.Geocoder();

function initializePage(places) {
    let markers = [];   // 생성한 마커를 저장한다.
    let remainingRequests = places.length;   // 주소 정보를 가져올 데이터의 개수
    let latList = [];
    let lngList = [];

    // 반복문을 통해 데이터의 주소를 좌표로 변환한다.
    places.forEach(function(place) {
        // 주소 정보가 공백인지 확인한다.
        if (!place.address || place.address.trim() === "") {
            remainingRequests--; // 1개의 데이터를 확인했으므로 데이터 -1
            checkAndAddMarkers();   // 데이터 처리 완료됐는지 확인 후 마커를 클러스터러에 추가한다.
            return; // 공백인 경우 다음 데이터를 확인한다.
        }

        // 주소를 좌표로 변환한다.
        geocoder.addressSearch(place.address, function(result, status) {
            // 정상적으로 검색이 완료됐으면
            if (status === kakao.maps.services.Status.OK) {
                // 변환한 좌표
                let coords = new kakao.maps.LatLng(result[0].y, result[0].x);
                
                // 마커를 생성한다.
                let marker = new kakao.maps.Marker({
                    position: coords
                });

                // 추천 여행지 리스트에 장소를 추가한다.
                let li = document.createElement("li");
                let divPlaceName = document.createElement("div");
                let img = document.createElement("img");

                divPlaceName.textContent = place.name;
                divPlaceName.className = "place-name"
                img.src = "https://pet-place-bucket.s3.ap-northeast-2.amazonaws.com/" + place.name + '.jpg';
                li.appendChild(divPlaceName);
                li.appendChild(img);
                document.querySelector('#place-list').appendChild(li);
                
                overlay.setMap(null);

                // 생성한 마커를 배열에 추가한다.
                markers.push(marker);                 
                latList.push(coords.getLat());
                lngList.push(coords.getLng());

                if (!place.short_info || place.short_info === "정보없음") {
                    place.short_info = "해당 장소의 정보를 찾을 수 없습니다."
                }

                // 마커에 클릭 이벤트를 등록한다.
                kakao.maps.event.addListener(marker, 'click', () => {
                    if(overlay.getVisible()) {
                        overlay.setMap(null);
                    } 
                    
                    message = 
                    '   <div id="info">'+ 
                    '       <strong>' + place.name + '</strong>' + "</br>" +
                    '       <strong>' + "\"" + place.short_info + "\"" + '</strong>' +
                    '       <p>' + place.address + '</p>'+ 
                    '   </div>' + '</br></br></br>' + 
                    '   <button type="button" id="add-btn" onclick="addPlace()">추가하기</button>' +
                    '   <button type="button" id="close-btn" onclick="closeInfo()">닫기</button>' ;
  
                    overlay.setContent(message);
                    overlay.setPosition(coords);  
                    overlay.setMap(map);
                    map.setCenter(coords);
                    map.setLevel(7);
                });

                divPlaceName.addEventListener('click', () => {
                    if(overlay.getVisible()) {
                        overlay.setMap(null);
                    }

                    message = 
                    '   <div id="info">'+ 
                    '       <strong>' + place.name + '</strong>' + "</br>" +
                    '       <strong>' + "\"" + place.short_info + "\"" + '</strong>' +
                    '       <p>' + place.address + '</p>'+ 
                    '   </div>' + '</br></br></br>' + 
                    '   <button type="button" id="add-btn" onclick="addPlace()">추가하기</button>' +
                    '   <button type="button" id="close-btn" onclick="closeInfo()">닫기</button>' ;
  
                    overlay.setContent(message);
                    overlay.setPosition(coords);  
                    overlay.setMap(map);
                    map.setCenter(coords);
                    map.setLevel(7);
                });
            };
            
            remainingRequests--;    // 데이터 처리 했으므로 개수 -1
            checkAndAddMarkers();   // 데이터 처리 완료됐는지 확인 후 마커를 클러스터러에 추가한다.
            getAvgMarker();
        });
    });

    function checkAndAddMarkers() {
        if (remainingRequests === 0) {  // 데이터 개수 = 0 : 모든 데이터를 확인
                clusterer.addMarkers(markers);  // 마커를 클러스터러에 추가한다.
            }
    }

    function getAvgMarker() {
        let sumLat = 0;
        let sumLng = 0;
        let avgLat = 0;
        let avgLng = 0;

        if (latList.length === places.length) {
            for(let i=0; i < places.length; i++) {
                sumLat += latList[i];
                sumLng += lngList[i];
            }

            avgLat = sumLat / (places.length);
            avgLng = sumLng / (places.length);

            let avgMarker = new kakao.maps.Marker({
                position: new kakao.maps.LatLng(avgLat, avgLng)
            });

            map.setCenter(avgMarker.getPosition());
            map.setLevel(12);

            let showAllMarker = document.getElementById('show-all-place-marker');
            showAllMarker.addEventListener('click', () => {
                map.setCenter(avgMarker.getPosition());
                map.setLevel(12);
            });
        }
    }
    
}

// 현재 위치를 받아오는 함수
function getCurrentLocation() {
    if (navigator.geolocation) {
        (navigator.geolocation.getCurrentPosition(function(position) {
            let coords = new kakao.maps.LatLng(position.coords.latitude, position.coords.longitude);

            // 마커 이미지 정보
            let currentLocImgSrc = "/static/data/current_location_icon.png"; // 마커 이미지 주소
            let currentLocImgSize = new kakao.maps.Size(22, 26);   // 마커 이미지 크기
            // 마커 이미지를 생성한다.
            let markerImg = new kakao.maps.MarkerImage(currentLocImgSrc, currentLocImgSize);
            
            // 마커를 지도에 표시한다.
            let marker = new kakao.maps.Marker({
                map: map,
                position: coords,
                image: markerImg
            });

            map.setCenter(coords);
            map.setLevel(13);
            
            kakao.maps.event.addListener(marker, 'click', () => {
                map.setCenter(coords);
                map.setLevel(13);
            });
        }))
    }
    // const getIp = async () =>
    //     await fetch("https://geolocation-db.com/json/")
    //         .then((res) => res.json())
    //         .then((res) => {
    //             const nowIp = res["IPv4"];
    //             // console.log(res["IPv4"]);
    //             fetch(`http://ip-api.com/json/${nowIp}`)
    //             .then((res) => res.json())
    //             .then((res) => {
    //                 let lat = res["lat"];
    //                 let lng = res["lon"];
    //                 let coords = new kakao.maps.LatLng(lat, lng);

    //                 // 마커 이미지 정보
    //                 let currentLocImgSrc = "/static/data/current_location_icon.png"; // 마커 이미지 주소
    //                 let currentLocImgSize = new kakao.maps.Size(22, 26);   // 마커 이미지 크기
    //                 // 마커 이미지를 생성한다.
    //                 let markerImg = new kakao.maps.MarkerImage(currentLocImgSrc, currentLocImgSize);
                    
    //                 // 마커를 지도에 표시한다.
    //                 let marker = new kakao.maps.Marker({
    //                     map: map,
    //                     position: coords,
    //                     image: markerImg
    //                 });

    //                 map.setCenter(coords);
    //                 map.setLevel(13);
                    
    //                 kakao.maps.event.addListener(marker, 'click', () => {
    //                     map.setCenter(coords);
    //                     map.setLevel(13);
    //                 });
    //             })
    //         })
    //     getIp();
}

// 정보창(커스텀 오버레이)를 닫는 함수
function closeInfo() {
    // 정보창을 숨긴다.
    overlay.setMap(null);
}

function route() {
    let start = document.querySelector('.course-list > li').textContent.replace("삭제", "");
    let end = document.querySelector('.course-list li:nth-child(2)').textContent.replace("삭제", "");

    window.open("https://map.kakao.com/?sName="+start+"&eName="+end);
}

function addPlace() {
    if (currentUsername) {
        // 리스트에서 목록을 가져온다.
        let liElements = document.querySelectorAll('#course-list li');
        // 리스트에 넣을 내용(장소 이름)을 지도의 정보창에서 가져와서 content에 저장한다.
        let placeName = document.querySelector('#info strong').textContent;   
        let placeAddress = document.querySelector('#info p').textContent;     
        let exists = false; // 목록 내용의 중복을 확인하기 위한 변수
        
        // 리스트에 중복 내용이 있는지 검사한다.
        for (let i = 0; i < liElements.length; i++) {
            // 리스트 내용의 첫번째 자식 요소(장소 이름)과 content가 같으면
            if (liElements[i].firstChild && liElements[i].firstChild.textContent === placeName) {
                exists = true;  // 중복이 존재한다.
                break;
            } 
        }
        
        if (exists) {   // 중복이 존재하면 중복 알림창 생성
            alert("이미 존재하는 코스입니다.");
        } else {    // 중복이 존재하지 않으면
            let li = document.createElement("li");  // li 요소를 생성한다.
            let p_name = document.createElement("p");
            let p_address = document.createElement("p");
            let img = document.createElement("img");
            // 삭제 버튼 생성
            let delBtn = document.createElement("button");  

            delBtn.className = "del-btn"; // 클래스를 할당하여 여러 개의 삭제 버튼이 생성되었을 때 올바르게 동작하도록 함
            delBtn.textContent = "삭제";
            delBtn.style.float = "right";
            delBtn.onclick = function() {
                this.parentNode.remove();
                
            };
            img.src = "https://pet-place-bucket.s3.ap-northeast-2.amazonaws.com/" + placeName + '.jpg';
            p_name.textContent = placeName
            p_name.className = "course-place-name";
            p_address.textContent = placeAddress;
            p_address.className = "course-place-address";

            // li.textContent = placeName ;   // 장소의 이름과 주소를 리스트에 추가한다.
            li.append(p_name, delBtn, p_address, img);
            document.querySelector('#course-list').appendChild(li);
            
            geocoder.addressSearch(placeAddress, function(result, status) {
                if (status === kakao.maps.services.Status.OK) {
                    // 변환한 좌표
                    let coords = new kakao.maps.LatLng(result[0].y, result[0].x);
                    
                    path.push(coords);
                    let line = new kakao.maps.Polyline({
                        map:map, 
                        path: path,
                        strokeWeight: 3, // 선의 두께입니다 
                        strokeColor: '#db4040', // 선의 색깔입니다
                        strokeOpacity: 1, // 선의 불투명도입니다 0에서 1 사이값이며 0에 가까울수록 투명합니다
                        strokeStyle: 'solid' // 선의 스타일입니다
                    });
                }
            })

            p_name.addEventListener('click', () => {
                geocoder.addressSearch(p_address.textContent, function (result, status) {
                    // 정상적으로 검색이 완료됐으면
                    if (status === kakao.maps.services.Status.OK) {
                        // 변환한 좌표
                        let coords = new kakao.maps.LatLng(result[0].y, result[0].x);
                        if (navigator.geolocation) {
                            // geolocation으로 현재 위치를 받는다.
                            navigator.geolocation.getCurrentPosition(function(position) {
                                let marker = new kakao.maps.Marker({
                                    position: coords
                                });

                                if (overlay.getVisible()) {
                                    overlay.setMap(null);
                                }
                                message =
                                '   <div id="info">'+ 
                                '       <strong>' + p_name.textContent + '</strong>' + "</br>" +'<p>'+ p_address.textContent + '</p>'+ '</div>' + '</br></br></br>' + 
                                '   <button type="button" id="add-btn" onclick="addPlace()">추가하기</button>' +
                                '   <button type="button" id="close-btn" onclick="closeInfo()">닫기</button>';
                                overlay.setContent(message);
                                overlay.setPosition(coords);  
                                overlay.setMap(map);
                                map.setCenter(coords);
                                map.setLevel(5);
                            })
                        }
                    }
                })
            })// '나의 여행 코스'에 추가한다.
        }
    } else {
        alert("로그인 후 이용할 수 있습니다.");
    };
}