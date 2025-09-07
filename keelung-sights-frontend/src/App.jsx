import { useEffect, useState } from "react";

const API_BASE = "http://localhost:8080/SightAPI/db?zone=";
const ZONES = ["中山", "信義", "仁愛", "中正", "安樂", "七堵", "暖暖"];

function Modal({ isOpen, onClose, children }) {
    if (!isOpen) return null;

    return (
        <div
            className="fixed inset-0 bg-black bg-opacity-50 z-50 flex justify-center items-center p-4"
            onClick={onClose}
        >
            <div
                className="bg-white rounded-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto"
                onClick={e => e.stopPropagation()}
            >
                {children}
            </div>
        </div>
    );
}

function SightsList() {
    const [sights, setSights] = useState([]);
    const [selectedZone, setSelectedZone] = useState("中山");
    const [selectedSight, setSelectedSight] = useState(null);

    useEffect(() => {
        fetch(API_BASE + selectedZone)
            .then((res) => res.json())
            .then((data) => {
                console.log("API 回傳資料：", data);
                setSights(data);
            })
            .catch((err) => console.error("API 錯誤：", err));
    }, [selectedZone]);

    return (
        <div className="min-h-screen w-full flex justify-center">
            <div className="w-full max-w-7xl px-4 py-8">
                <h1 className="text-4xl font-bold text-center mb-8 text-white">基隆景點導覽</h1>
                <div className="mb-8 flex flex-wrap justify-center items-center gap-2">
                    {ZONES.map((zone) => (
                        <button
                            key={zone}
                            className={`px-4 py-2 rounded text-white transition-colors ${selectedZone === zone ? 'bg-blue-700' : 'bg-blue-500 hover:bg-blue-600'}`}
                            onClick={() => setSelectedZone(zone)}
                        >
                            {zone}區
                        </button>
                    ))}
                </div>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    {sights.length === 0 ? (
                        <p className="text-center col-span-full">沒有資料（或請檢查後端是否回傳空陣列）。</p>
                    ) : (
                        sights.map((s, i) => (
                            <div
                                key={i}
                                className="w-full bg-white rounded-xl shadow-lg overflow-hidden transition-transform hover:scale-[1.02] duration-300"
                                style={{ height: 'fit-content' }}
                            >
                                <div className="aspect-w-16 aspect-h-9">
                                    <img
                                        src={s.photoURL}
                                        alt={s.sightName}
                                        className="w-full h-48 object-cover"
                                    />
                                </div>
                                <div className="p-4">
                                    <h2 className="font-bold text-xl text-black mb-2 text-center">{s.sightName}</h2>
                                    <div className="text-sm text-gray-600 mb-3 text-center">
                                        區域：{s.zone}　分類：{s.category}
                                    </div>
                                    <div className="flex gap-2 justify-center">
                                        <a
                                            href={`https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(s.address)}`}
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            className="px-4 py-2 bg-green-500 text-white rounded-full hover:bg-green-600 text-center shadow transition-colors"
                                        >
                                            地址
                                        </a>
                                        <button
                                            className="px-4 py-2 bg-blue-500 text-white rounded-full hover:bg-blue-600 text-center shadow transition-colors"
                                            onClick={() => setSelectedSight(s)}
                                        >
                                            詳細資訊
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>

            <Modal
                isOpen={!!selectedSight}
                onClose={() => setSelectedSight(null)}
            >
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
    );
}

export default SightsList;
