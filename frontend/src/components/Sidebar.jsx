// src/components/Sidebar.jsx
import { useNavigate } from "react-router-dom";

export default function Sidebar() {
  const navigate = useNavigate();

  const goToCompose = () => {
    navigate("/compose");
  };

  const menuItems = [
    { label: "Home", icon: "🏠" },
    { label: "Explore", icon: "🔍" },
    { label: "Notifications", icon: "🔔" },
    { label: "Messages", icon: "✉️" },
    { label: "Bookmarks", icon: "📑" },
    { label: "Profile", icon: "🙂" ,path: "/userpage"},
  ];

  // Widgets.jsx의 Who to follow 글씨체 스타일 그대로 적용
  const menuTextStyle = {
    fontSize: "16px",
    fontWeight: 600,
  };

  return (
    <div
      style={{
        flex: 1,
        maxWidth: "250px",
        padding: "20px",
        borderRight: "1px solid #eee",
        height: "100vh",
        position: "sticky",
        top: 0,
      }}
    >
      {/* X 로고 */}
      <div
        style={{
          fontSize: "30px",
          marginBottom: "30px",
          cursor: "pointer",
        }}
      >
        𝕏
      </div>

      {/* 메뉴 */}
      <div>
        {menuItems.map((item) => (
          <div
            key={item.label}
            onClick={() => item.path && navigate(item.path)}
            style={{
              display: "flex",
              alignItems: "center",
              gap: "15px",
              padding: "12px 0",
              cursor: "pointer",
              borderRadius: "30px",
              transition: "0.2s",
            }}
          >
            <span style={{ fontSize: "20px" }}>{item.icon}</span>
            <span style={menuTextStyle}>{item.label}</span>
          </div>
        ))}
      </div>

      {/* Post 버튼 */}
      <button
        onClick={goToCompose}
        style={{
          marginTop: "20px",
          backgroundColor: "black",
          color: "white",
          padding: "15px 20px",
          borderRadius: "30px",
          width: "100%",
          fontSize: "18px",
          border: "none",
          cursor: "pointer",
          fontWeight: "600",
        }}
      >
        Post
      </button>
    </div>
  );
}
