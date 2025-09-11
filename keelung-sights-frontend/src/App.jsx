import { useEffect, useState } from "react";

const API_BASE = "https://keelung-production-7cdf.up.railway.app/SightAPI/db?zone=";
const ZONES = ["中山", "信義", "仁愛", "中正", "安樂", "七堵", "暖暖"];

// ===== Modal 元件 (可重用) =====
function Modal({ isOpen, onClose, children }) {
  if (!isOpen) return null;

  return (
    <div
      className="fixed inset-0 bg-black bg-opacity-50 z-50 flex justify-center items-center p-4"
      onClick={onClose}
      role="dialog"
      aria-modal="true"
    >
      <div
        className="bg-white rounded-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto"
        onClick={(e) => e.stopPropagation()}
      >
        {children}
      </div>
    </div>
  );
}

// ===== 景點卡片元件 =====
function SightCard({ sight, onDetail }) {
  return (
    <div className="w-full bg-white rounded-xl shadow-lg overflow-hidden transition-transform hover:scale-[1.02] duration-300 h-fit">
      <div className="aspect-w-16 aspect-h-9">
        <img
          src={sight.photoURL}
          alt={sight.sightName}
          className="w-full h-48 object-cover"
        />
      </div>
      <div className="p-4">
        <h2 className="font-bold text-xl text-black mb-2 text-center">{sight.sightName}</h2>
        <div className="text-sm text-gray-600 mb-3 text-center">
          區域：{sight.zone}　分類：{sight.category}
        </div>
        <div className="flex gap-2 justify-center">
          <a
            href={`https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(sight.address)}`}
            target="_blank"
            rel="noopener noreferrer"
            className="px-4 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 text-center shadow transition-colors"
          >
            地址
          </a>
          <button
            className="px-4 py-2 bg-blue-500 text-white rounded-full hover:bg-blue-600 text-center shadow transition-colors"
            onClick={() => onDetail(sight)}
          >
            詳細資訊
          </button>
        </div>
      </div>
    </div>
  );
}

// ===== 主元件 =====
function SightsList() {
  const [sights, setSights] = useState([]);
  const [selectedZone, setSelectedZone] = useState("中山");
  const [selectedSight, setSelectedSight] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // 取得景點資料
  useEffect(() => {
    setLoading(true);
    setError(null);
    fetch(API_BASE + selectedZone)
      .then((res) => res.json())
      .then((data) => setSights(data))
      .catch((err) => setError("資料讀取失敗"))
      .finally(() => setLoading(false));
  }, [selectedZone]);

  return (
    <div className="min-h-screen w-full flex justify-center">
      <div className="w-full px-4 py-8">
        <h1 className="text-4xl font-bold text-center mb-8 text-white">基隆景點導覽</h1>

        {/* 區域選擇 */}
        <div className="mb-8 flex flex-wrap justify-center items-center gap-2">
          {ZONES.map((zone) => (
            <button
              key={zone}
              className={`px-4 py-2 rounded text-white transition-colors ${selectedZone === zone ? "bg-blue-700" : "bg-blue-500 hover:bg-blue-600"}`}
              onClick={() => setSelectedZone(zone)}
            >
              {zone}區
            </button>
          ))}
        </div>

        {/* 景點列表 */}
        {loading ? (
          <p className="text-center">讀取中...</p>
        ) : error ? (
          <p className="text-center text-red-500">{error}</p>
        ) : sights.length === 0 ? (
          <p className="text-center">沒有資料（或請檢查後端是否回傳空陣列）。</p>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {sights.map((s) => (
              <SightCard key={s.id || s.sightName} sight={s} onDetail={setSelectedSight} />
            ))}
          </div>
        )}

        {/* Modal 詳細資訊 */}
        <Modal isOpen={!!selectedSight} onClose={() => setSelectedSight(null)}>
          {selectedSight && (
            <div className="p-6">
              <div className="relative">
                <img
                  src={selectedSight.photoURL}
                  alt={selectedSight.sightName}
                  className="w-full rounded-lg shadow-lg mb-4"
                />
                <button
                  onClick={() => setSelectedSight(null)}
                  className="absolute top-2 right-2 bg-black bg-opacity-50 text-white w-8 h-8 rounded-full flex items-center justify-center hover:bg-opacity-70 transition-colors"
                >
                  ✕
                </button>
              </div>
              <h2 className="text-2xl text-black font-bold mb-3">{selectedSight.sightName}</h2>
              <div className="text-gray-600 mb-2">
                <span className="font-semibold">區域：</span>{selectedSight.zone}
              </div>
              <div className="text-gray-600 mb-2">
                <span className="font-semibold">分類：</span>{selectedSight.category}
              </div>
              <div className="text-gray-600 mb-4">
                <span className="font-semibold">地址：</span>{selectedSight.address}
              </div>
              <div className="text-gray-700 whitespace-pre-line">
                {selectedSight.description}
              </div>
            </div>
          )}
        </Modal>
      </div>
    </div>
  );
}

export default SightsList;
