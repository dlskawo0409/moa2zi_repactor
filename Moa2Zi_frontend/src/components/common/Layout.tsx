import { Outlet, useMatches } from "react-router-dom";
import Header from "./Header";
import Footer from "./Footer";

interface RouteHandle {
  header?: boolean;
  footer?: boolean;
}

const Layout = () => {
  const routeMatch = useMatches().find((match) => match.handle);
  const layoutConfig: RouteHandle = routeMatch?.handle || {};
  const headerComponent = (layoutConfig.header ?? true) ? <Header /> : null;
  const footerComponent = (layoutConfig.footer ?? true) ? <Footer /> : null;

  return (
    <div className="flex flex-col min-w-[300px] w-full max-w-[600px] h-full min-h-screen mx-auto bg-white pc:border-x border-neutral-200">
      {headerComponent}
      <main
        className={`flex flex-col flex-1 ${headerComponent ? "pt-14" : ""} ${
          footerComponent ? "pb-16" : ""
        }`}
      >
        <Outlet />
      </main>
      {footerComponent}
    </div>
  );
};

export default Layout;
