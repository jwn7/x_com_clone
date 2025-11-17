// src/components/Widgets.jsx

export default function Widgets() {
    const suggestions = [
      { name: "Khalid", handle: "@khaled3" },
      { name: "Samy", handle: "@samy" },
      { name: "Amin Yasser", handle: "@amin" },
    ];
  
    return (
      <div
        style={{
          flex: 1,
          padding: "20px",
          maxWidth: "350px",
        }}
      >
        {/* 검색 바 */}
        <div
          style={{
            display: "flex",
            alignItems: "center",
            backgroundColor: "#f3f4f6",
            borderRadius: "999px",
            padding: "8px 12px",
            marginBottom: "16px",
          }}
        >
          <span style={{ marginRight: "8px", fontSize: "16px" }}>🔍</span>
          <input
            type="text"
            placeholder="Search Twitter"
            style={{
              border: "none",
              outline: "none",
              backgroundColor: "transparent",
              fontSize: "14px",
              width: "100%",
            }}
          />
        </div>
  
        {/* Who to follow 카드 */}
        <div
          style={{
            backgroundColor: "#f5f8fa",
            borderRadius: "16px",
            padding: "12px 16px",
          }}
        >
          <div
            style={{
              fontSize: "16px",
              fontWeight: "600",
              marginBottom: "12px",
            }}
          >
            Who to follow
          </div>
  
          {suggestions.map((user) => (
            <div
              key={user.handle}
              style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                marginBottom: "10px",
              }}
            >
              {/* 왼쪽: 프로필 + 이름 */}
              <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
                <div
                  style={{
                    width: "40px",
                    height: "40px",
                    borderRadius: "50%",
                    backgroundColor: "#d1d5db",
                  }}
                />
                <div>
                  <div style={{ fontSize: "14px", fontWeight: 600 }}>
                    {user.name}
                  </div>
                  <div style={{ fontSize: "13px", color: "#6b7280" }}>
                    {user.handle}
                  </div>
                </div>
              </div>
  
              {/* 오른쪽: Follow 버튼 */}
              <button
                style={{
                  padding: "6px 12px",
                  borderRadius: "999px",
                  border: "none",
                  backgroundColor: "#000",
                  color: "#fff",
                  fontSize: "13px",
                  cursor: "pointer",
                }}
              >
                Follow
              </button>
            </div>
          ))}
        </div>
      </div>
    );
  }
  