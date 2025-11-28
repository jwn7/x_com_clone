// 전역 메시지 함수
window.showMessage = function(message) {
    console.log("Message: " + message);
    let banner = document.querySelector('.message-banner');

    // 배너가 없으면 생성
    if (!banner) {
        banner = document.createElement('div');
        banner.className = 'message-banner success';
        document.body.appendChild(banner);
    }

    banner.textContent = message;
    banner.style.display = 'block';

    setTimeout(() => {
        banner.style.display = 'none';
    }, 3000);
};

// 게시글 작성
window.createPost = async function(event) {
    event.preventDefault();
    const content = document.getElementById('postContent').value.trim();
    const fileInput = document.getElementById('postFiles');
    const files = fileInput ? fileInput.files : [];

    if (!content && (!files || files.length === 0)) {
        showMessage("내용이나 이미지를 입력해 주세요.");
        return;
    }

    const formData = new FormData();
    formData.append("content", content);
    for (let i = 0; i < files.length; i++) {
        formData.append("mediaFiles", files[i]);
    }

    try {
        const response = await fetch('/posts', { method: 'POST', body: formData });
        if (response.ok || response.redirected) window.location.reload();
        else showMessage("작성 실패: " + await response.text());
    } catch (error) {
        showMessage("네트워크 오류");
    }
};

// 게시글 삭제
window.deletePost = async function(postId) {
    if (!confirm('삭제하시겠습니까?')) return;
    try {
        const response = await fetch(`/posts/${postId}/delete`, { method: 'POST' });
        if (response.ok) {
            const el = document.getElementById(`item-${postId}`);
            if (el) el.remove();
            showMessage("삭제되었습니다.");
        } else {
            showMessage("삭제 실패");
        }
    } catch (error) {
        showMessage("오류 발생");
    }
};