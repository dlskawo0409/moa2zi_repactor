import { ReactNode, Dispatch, SetStateAction } from "react";

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";

interface CommonModalProps {
  className?: string;
  open?: boolean;
  setOpen?: Dispatch<SetStateAction<boolean>>;
  trigger: ReactNode;
  title?: string;
  children: ReactNode;
  footerComponent?: ReactNode;
}

const CommonModal = ({
  className,
  open,
  setOpen,
  trigger,
  title,
  children,
  footerComponent,
}: CommonModalProps) => {
  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger>{trigger}</DialogTrigger>
      <DialogContent className={className}>
        {title ? (
          <DialogHeader>
            <DialogTitle>{title}</DialogTitle>
          </DialogHeader>
        ) : (
          <DialogHeader className="hidden">
            <DialogTitle></DialogTitle>
          </DialogHeader>
        )}
        <DialogDescription className="hidden"></DialogDescription>
        {children}
        {footerComponent && <DialogFooter>{footerComponent}</DialogFooter>}
      </DialogContent>
    </Dialog>
  );
};

export default CommonModal;
