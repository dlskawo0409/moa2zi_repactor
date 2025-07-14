import EmergenceMouseIcon from "@/components/svgs/EmergenceMouseIcon";

const MapAlertBar = ({ content }: { content: string }) => {
  return (
    <div className="relative text-sm pc:text-m top-16 flex justify-center h-12 px-4 py-2">
      <div className="flex gap-2 items-center z-[50] w-full bg-white px-4 py-2 shadow-lg border border-primary-500 rounded-3xl text-negative-800">
        <div className="flex justify-center items-center w-8 h-8">
          <EmergenceMouseIcon className="w-6 h-6" />
        </div>
        <span className="text-negative-800">{content}</span>
      </div>
    </div>
  );
};

export default MapAlertBar;
