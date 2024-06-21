async function sendToFastAPI(event) {
    event.preventDefault(); // Prevent form default behavior

    // Get form field values
    const city = document.getElementById('citySelect').value.trim();
    const district = document.getElementById('districtSelect').value.trim();
    const checkboxes = document.querySelectorAll('input[name="theme"]:checked');
    const selectedThemes = Array.from(checkboxes).map(checkbox => checkbox.value);

    // Check if user is logged in
    if (!currentUsername) {
        alert("로그인 후 이용할 수 있습니다.");
        loginPage();
        return;
    }

    // Check if all fields are filled
    if (!city || !district || !selectedThemes) {
        alert("모든 필드를 입력해주세요.");
        return;
    }

    // Combine input text
    const input_text = `${city} ${district} ${selectedThemes.join(', ')}`;

    try {
        const response = await fetch('https://api.petwme.com/search', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ userID: currentUsername, input_text: input_text, city: city })
        });

        if (response.ok) {
            const result = await response.json();
            // alert(JSON.stringify(result.results));
            const places = result.results;
            const queryParams = new URLSearchParams();
            queryParams.append('result', JSON.stringify(places))
            alert("추천 여행지 리스트로 이동합니다.");
            window.location.href = `recTravelPlan?${queryParams.toString()}`;

        } else {
            alert("모든 필드를 입력해주세요.");
        }
    } catch (error) {
        console.error('Error:', error);
        alert("모든 필드를 입력해주세요.");
    }
}