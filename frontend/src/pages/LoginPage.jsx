// src/pages/LoginPage.jsx

import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function LoginPage() {
  const [id, setId] = useState("");
  const [pw, setPw] = useState("");
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("로그인 시도:", id, pw);

    // 일단은 무조건 성공했다고 치고 /feed로 이동
    navigate("/feed");
  };


  return (
    <div
      style={{
        display: "flex",
        width: "100vw",
        height: "100vh",
        backgroundColor: "#000000",
      }}
    >
      {/* 왼쪽 문구 영역 */}
      <div
        style={{
          flex: 1,
          backgroundColor: "#000000",
          color: "#ffffff",
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
        }}
      >
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            gap: "24px",
            fontSize: "20px",
          }}
        >
          <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
            <span style={{ fontSize: "24px" }}>💬</span>
            <span>Follow your interests.</span>
          </div>

          <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
            <span style={{ fontSize: "24px" }}>❤️</span>
            <span>Hear what people are talking about.</span>
          </div>

          <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
            <span style={{ fontSize: "24px" }}>💭</span>
            <span>Join the conversation.</span>
          </div>
        </div>
      </div>

      {/* 오른쪽 로그인 영역 */}
      <div
        style={{
          flex: 1,
          backgroundColor: "#ffffff",
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
        }}
      >
        <form
          onSubmit={handleSubmit}
          style={{
            display: "flex",
            flexDirection: "column",
            gap: "14px",
            width: "280px",
          }}
        >
          <img
            src="/x_logo.png"
            alt="X logo"
            style={{
              width: "40px",
              height: "40px",
              marginBottom: "0px",
            }}
          />

          <div
            style={{
              marginBottom: "40px",
              display: "flex",
              flexDirection: "column",
              lineHeight: "1.3",
            }}
          >
            <span style={{ fontSize: "25px", fontWeight: "600" }}>
              See what's happening
            </span>
            <span style={{ fontSize: "25px", fontWeight: "600" }}>
              in the world right now
            </span>
          </div>

          <input
            type="text"
            placeholder="ID"
            value={id}
            onChange={(e) => setId(e.target.value)}
            style={{
              padding: "12px",
              fontSize: "14px",
              borderRadius: "6px",
              border: "1px solid #cccccc",
            }}
          />

          <input
            type="password"
            placeholder="Password"
            value={pw}
            onChange={(e) => setPw(e.target.value)}
            style={{
              padding: "12px",
              fontSize: "14px",
              borderRadius: "6px",
              border: "1px solid #cccccc",
            }}
          />

          <button
            type="submit"
            style={{
              padding: "12px",
              borderRadius: "6px",
              border: "none",
              cursor: "pointer",
              backgroundColor: "#000000",
              color: "#ffffff",
              fontSize: "15px",
              fontWeight: "600",
            }}
          >
            Login
          </button>
        </form>
      </div>
    </div>
  );
}
