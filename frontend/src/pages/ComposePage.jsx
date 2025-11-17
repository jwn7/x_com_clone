// src/pages/ComposePage.jsx
// src/pages/ComposePage.jsx
import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function ComposePage() {
  const navigate = useNavigate();
  const [text, setText] = useState("");

  //실제로 서버에 보내는 부분은 백엔드랑 연결하면서 수정
  const handlePost = () => {
    if (!text.trim()) return;
    alert("임시로 알림만 실제로는 서버에 보내면 됨.");
    navigate("/feed");
  };

  const handleClose = () => {
    navigate("/feed");
  };

  return (
    <div style={styles.overlay}>
      <div style={styles.modal}>
        {/* 상단 X 버튼  */}
        <div style={styles.header}>
          <button style={styles.closeButton} onClick={handleClose}>
            ×
          </button>
        </div>

        {/* 프로필 + 입력창 */}
        <div style={styles.body}>
          <img
            src="https://via.placeholder.com/40"
            alt="profile"
            style={styles.profileImg}
          />

          <textarea
            style={styles.textarea}
            placeholder="What’s happening?"
            value={text}
            onChange={(e) => setText(e.target.value)}
          />
        </div>

        {/* Everyone can reply */}
        <div style={styles.replyRow}>
          <span style={{ fontSize: "18px", marginRight: "8px" }}>🌐</span>
          <span style={{ color: "black", fontSize: "14px" }}>
            Everyone can reply
          </span>
        </div>

        {/* 하단 아이콘 + Post 버튼 */}
        <div style={styles.footer}>
          <div style={styles.iconRow}>
            <span style={styles.icon}>🖼️</span>
            <span style={styles.icon}>GIF</span>
            <span style={styles.icon}>📊</span>
            <span style={styles.icon}>😄</span>
            <span style={styles.icon}>📍</span>
            <span style={styles.icon}>⏱️</span>
          </div>

          <button
            style={{
              ...styles.postButton,
              backgroundColor: text.trim() ? "#000000" : "#c4c4c4",
              cursor: text.trim() ? "pointer" : "default",
            }}
            onClick={handlePost}
            disabled={!text.trim()}
          >
            Post
          </button>
        </div>
      </div>
    </div>
  );
}

const styles = {
  overlay: {
    position: "fixed",
    inset: 0,
    backgroundColor: "rgba(0,0,0,0.4)",
    display: "flex",
    justifyContent: "center",
    alignItems: "flex-start",
    paddingTop: "40px",
    zIndex: 999,
  },
  modal: {
    width: "600px",
    backgroundColor: "white",
    borderRadius: "16px",
    boxShadow: "0 8px 24px rgba(0,0,0,0.2)",
    padding: "12px 16px 12px 16px",
    display: "flex",
    flexDirection: "column",
    maxHeight: "80vh",
  },
  header: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    marginBottom: "8px",
  },
  closeButton: {
    border: "none",
    background: "transparent",
    fontSize: "24px",
    cursor: "pointer",
  },
  draftsText: {
    fontSize: "14px",
    color: "#657786",
  },
  body: {
    display: "flex",
    marginTop: "8px",
  },
  profileImg: {
    width: "40px",
    height: "40px",
    borderRadius: "50%",
    marginRight: "12px",
  },
  textarea: {
    flex: 1,
    border: "none",
    resize: "none",
    fontSize: "20px",
    outline: "none",
    minHeight: "120px",
  },
  replyRow: {
    marginLeft: "52px",
    marginTop: "4px",
    paddingBottom: "8px",
    borderBottom: "1px solid #e1e8ed",
    display: "flex",
    alignItems: "center",
  },
  footer: {
    marginTop: "8px",
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
  },
  iconRow: {
    display: "flex",
    gap: "10px",
    marginLeft: "40px",
  },
  icon: {
    fontSize: "20px",
    cursor: "pointer",
  },
  postButton: {
    border: "none",
    color: "white",
    padding: "8px 18px",
    borderRadius: "999px",
    fontWeight: "600",
    fontSize: "15px",
  },
};
