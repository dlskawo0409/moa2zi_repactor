import { useEffect } from "react";
import { toast } from "sonner";

export const useTestAlert = (enabled: boolean = true) => {
  useEffect(() => {
    if (!enabled) return;

    const id = toast.error("이 근처에서 과소비한 적이 있어요!", {
      duration: 5000,
    });

    return () => {
      toast.dismiss(id);
    };
  }, [enabled]);
};
