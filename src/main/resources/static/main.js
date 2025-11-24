/* src/main/resources/static/js/main.js */

// Helper function to show a custom message box instead of alert()
function showMessage(message) {
    console.log("Message: " + message);
    alert(message);
}

// --- CRUD Functions ---

/**
 * 게시물을 생성하는 함수 (Create)
 * POST /api/posts 엔드포인트 호출 (multipart/form-data)
 */
async function createPost(event) {
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
            formData.append("files", files[i]);
        }
    }

    try {
        const response = await fetch('/api/posts', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            window.location.reload();
        } else if (response.status === 401) {
            showMessage("로그인이 필요합니다.");
        } else if (response.status === 404) {
            showMessage("게시물 작성 실패: 서버에서 경로(/api/posts)를 찾을 수 없습니다. (404 Not Found)");
        } else {
            const errorText = await response.text();
            showMessage("게시물 작성 실패: " + (errorText.length > 100 ? response.statusText : errorText));
        }
    } catch (error) {
        console.error('Error creating post:', error);
        showMessage("네트워크 오류 또는 서버 접속 실패가 발생했습니다.");
    }
}

/**
 * 게시물을 삭제하는 함수 (Delete)
 * DELETE /api/posts/{postId} 엔드포인트 호출
 */
async function deletePost(postId) {

    if (!confirm('정말 이 게시물을 삭제하시겠습니까?')) {
        return;
    }

    try {
        const response = await fetch(`/api/posts/${postId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const postElement = document.getElementById(`post-${postId}`);
            if (postElement) {
                postElement.remove();
            }
            showMessage("게시물이 성공적으로 삭제되었습니다.");
        } else if (response.status === 403) {
            showMessage("삭제 권한이 없습니다. 본인의 게시물만 삭제할 수 있습니다.");
        } else if (response.status === 404) {
            showMessage("게시물을 찾을 수 없습니다.");
        } else {
            const errorText = await response.text();
            showMessage("게시물 삭제 실패: " + errorText);
        }
    } catch (error) {
        console.error('Error deleting post:', error);
        showMessage("네트워크 오류가 발생했습니다.");
    }
}