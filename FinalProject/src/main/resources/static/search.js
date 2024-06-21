async function search(event) {
    if (!currentUsername) {
        alert("로그인 해주세요.");
        loginPage();
        return; // 로그인되지 않았을 때는 함수를 종료합니다.
    }

    const people = document.querySelector('select[name="people"]').value;
    const city = document.querySelector('select[name="city"]').value;
    const district = document.querySelector('select[name="district"]').value;
    const theme = document.querySelector('select[name="theme"]').value;

    const data = {
        userID: currentUsername,
        people: people,
        city: city,
        district: district,
        theme: theme
    };

    try {
        const response = await fetch('http://127.0.0.1:8000/searchbase', {
            method: 'POST', // POST 요청으로 변경
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ data: JSON.stringify(data) }) // "data" 키를 포함한 JSON 형식의 데이터를 보냅니다.
        });

        if (response.ok) {
            const result = await response.json();
            alert(JSON.stringify(result.message));
            // 검색 완료 후 추가 작업 수행
        } else {
            alert('검색 중 오류가 발생했습니다.');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('검색 중 오류가 발생했습니다.');
    }
}
