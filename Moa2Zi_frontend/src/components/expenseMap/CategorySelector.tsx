import { useEffect, useState } from "react";
import { ChevronDown } from "lucide-react";
import { getCategories } from "@/services/category";
import { Category } from "@/types/category";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Button } from "@/components/ui/button";

const CategorySelector = ({
  selectedCategory,
  setSelectedCategory,
}: {
  selectedCategory: number | null;
  setSelectedCategory: (id: number | null) => void;
}) => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [open, setOpen] = useState<boolean>(false);

  useEffect(() => {
    const fetch = async () => {
      const data = await getCategories(null, 0, "SPEND");
      setCategories(data);
    };
    fetch();
  }, []);

  const selected = categories.find((c) => c.categoryId === selectedCategory);

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button variant="outline" className="w-full justify-between">
          {selected ? selected.categoryName : "카테고리 선택"}
          <ChevronDown className="ml-2 h-4 w-4 opacity-50" />
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-[--radix-popover-trigger-width] p-2 space-y-1 max-h-[200px] overflow-y-auto">
        {categories.map(({ categoryId, categoryName }) => (
          <button
            key={categoryId}
            onClick={() => {
              setSelectedCategory(categoryId === selectedCategory ? null : categoryId);
              setOpen(false);
            }}
            className={`w-full text-left px-3 py-2 rounded-md hover:bg-primary-100 transition hover:font-bold ${
              selectedCategory === categoryId
                ? "bg-primary-500 text-white font-semibold"
                : "text-neutral-700"
            }`}
          >
            {categoryName}
          </button>
        ))}
      </PopoverContent>
    </Popover>
  );
};

export default CategorySelector;
