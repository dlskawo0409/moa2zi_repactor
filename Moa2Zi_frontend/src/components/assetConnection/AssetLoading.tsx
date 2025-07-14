const AssetLoading = () => {
  return (
    <div className="flex flex-col items-center justify-center h-screen bg-white">
      <img src="/logo.png" alt="logo" className="w-24 h-24 animate-pulse mb-6" />
      <div className="flex items-center gap-2 text-gray-500 text-sm">
        <div className="w-4 h-4 border-2 border-t-transparent border-gray-400 rounded-full animate-spin" />
        자산을 불러오는 중이에요...
      </div>
    </div>
  );
};

export default AssetLoading;
