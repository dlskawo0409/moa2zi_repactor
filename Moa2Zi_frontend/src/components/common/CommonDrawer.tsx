import { ReactNode } from "react";

import {
  Drawer,
  DrawerClose,
  DrawerContent,
  DrawerDescription,
  DrawerFooter,
  DrawerHeader,
  DrawerTitle,
  DrawerTrigger,
} from "@/components/ui/drawer";

interface CommonDrawerProps {
  trigger: ReactNode;
  header?: ReactNode;
  children: ReactNode;
  footer?: ReactNode;
}

const CommonDrawer = ({ trigger, header, children, footer }: CommonDrawerProps) => {
  return (
    <Drawer>
      <DrawerTrigger asChild>{trigger}</DrawerTrigger>
      <DrawerContent>
        <div className="mx-auto w-full max-w-[600px]">
          <DrawerHeader>
            <DrawerTitle>{header}</DrawerTitle>
            <DrawerDescription></DrawerDescription>
          </DrawerHeader>
          <div className="h-full">{children}</div>
          {footer && (
            <DrawerFooter>
              <DrawerClose>{footer}</DrawerClose>
            </DrawerFooter>
          )}
        </div>
      </DrawerContent>
    </Drawer>
  );
};

export default CommonDrawer;
