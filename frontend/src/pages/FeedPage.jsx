// src/pages/FeedPage.jsx
import Sidebar from "../components/Sidebar";
import Widgets from "../components/Widgets";

export default function FeedPage() {
  return (
    <div
      style={{
        display: "flex",
        width: "100%",
        minHeight: "100vh",
        backgroundColor: "#ffffff",
        boxSizing: "border-box",
      }}
    >
      {/* 왼쪽 사이드바 */}
      <aside
        style={{
          width: "260px",
          flexShrink: 0,
          borderRight: "1px solid #ddd",
        }}
      >
        <Sidebar />
      </aside>

      {/* 가운데 피드 영역: 남은 공간 전체 */}
      <main
        style={{
          flex: 1,
          padding: "24px 32px",
          display: "flex",
          alignItems: "flex-start",
        }}
      >
        <div style={{ fontSize: "20px" }}>
          피드 작성하는 페이지 입니다.
        </div>
      </main>

      {/* 오른쪽 위젯 영역 */}
      <section
        style={{
          width: "340px",
          flexShrink: 0,
          padding: "16px 24px",
        }}
      >
        <Widgets />
      </section>
    </div>
  );
}
