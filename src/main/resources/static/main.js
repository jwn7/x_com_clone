/* src/main/resources/static/js/main.js */

// 전역 메시지 함수
window.showMessage = function(message) {
    console.log("Message: " + message);
    alert(message);
};

// 게시글 작성 (AJAX)
window.createPost = async function(event) {
    event.preventDefault();

    const content = document.getElementById('postContent').value.trim();
    const fileInput = document.getElementById('postFiles');
    const files = fileInput ? fileInput.files : [];

    if (content === "" && (!files || files.length === 0)) {
        showMessage("게시물 내용이나 이미지를 하나 이상 입력해 주세요.");
        return;
    }

    const formData = new FormData();
    formData.append("content", content);

    if (files && files.length > 0) {
        for (let i = 0; i < files.length; i++) {
            formData.append("mediaFiles", files[i]);
        }
    }

    try {
        const response = await fetch('/posts', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            window.location.reload(); // 새로고침
        } else {
            const errorText = await response.text();
            showMessage("게시물 작성 실패: " + errorText);
        }
    } catch (error) {
        console.error(error);
        showMessage("네트워크 오류가 발생했습니다.");
    }
};

// 게시글 삭제 (전역)
window.deletePost = async function(postId) {
    if (!confirm('정말 이 게시물을 삭제하시겠습니까?')) return;

    try {
        const response = await fetch(`/posts/${postId}/delete`, {
            method: 'POST'
        });

        if (response.ok) {
            const postElement = document.getElementById(`item-${postId}`);
            if (postElement) postElement.remove();
            showMessage("게시물이 성공적으로 삭제되었습니다.");
        } else if (response.status === 403) {
            showMessage("삭제 권한이 없습니다.");
        } else if (response.status === 404) {
            showMessage("게시물을 찾을 수 없습니다.");
        } else {
            const errorText = await response.text();
            showMessage("게시물 삭제 실패: " + errorText);
        }
    } catch (error) {
        console.error(error);
        showMessage("네트워크 오류가 발생했습니다.");
    }
};
