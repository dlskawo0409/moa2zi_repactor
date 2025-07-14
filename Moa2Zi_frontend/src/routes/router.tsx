import { createBrowserRouter } from "react-router-dom";
import PublicRoute from "@/components/guards/PublicRoute";
import Home from "@/pages/Home";
import LoginPage from "@/pages/login/LoginPage";
import TermPage from "@/pages/login/TermPage";
import TermDetailPage from "@/pages/login/TermDetailPage";
import PhoneVerificationPage from "@/pages/login/PhoneVerificationPage";
import SignupPage from "@/pages/login/SignupPage";
import Layout from "@/components/common/Layout";
import RecommendChallenge from "@/components/challenge/RecommendChallenge";
import MyChallenge from "@/components/challenge/MyChallenge";
import AllChallenge from "@/components/challenge/AllChallenge";
import LoungeMainPage from "@/pages/lounge/LoungeMainPage";
import LoungeRoomPage from "@/pages/lounge/LoungeRoomPage";
import LoungeGamePage from "@/pages/lounge/LoungeGamePage";
import LoungeGameResultPage from "@/pages/lounge/LoungeGameResultPage";
import LoungeGameRankPage from "@/pages/lounge/LoungeGameRankPage";
import MyAccountBookPage from "@/pages/accountBook/MyAccountBookPage";
import LoungeStartPage from "@/pages/lounge/LoungeStartPage";
import AccountBookCalendarPage from "@/pages/calendar/AccountBookCalendarPage";
import AccountBookCalendarDatePage from "@/pages/calendar/AccountBookCalendarDatePage";
import PocketMoneyPage from "@/pages/accountBook/PocketMoneyPage";
import ExpenseStatisticsPage from "@/pages/accountBook/ExpenseStatisticsPage";
import CategoryStatisticsPage from "@/pages/accountBook/CategoryStatisticsPage";
import CategoryExpensePage from "@/pages/accountBook/CategoryExpensePage";
import MapPage from "@/pages/MapPage";
import ChallengePage from "@/pages/ChallengePage";
import AssetConnectionPage from "@/pages/assetConnection/AssetConnectionPage";
import SettingPage from "@/pages/SettingPage";
import ProfilePage from "@/pages/ProfilePage";
import FriendPage from "@/pages/friend/FriendPage";
import FriendCalenderPage from "@/pages/friend/FriendCalenderPage";
import FriendsFriendPage from "@/pages/friend/FriendsFriendPage";
import NotFoundPage from "@/pages/NotFoundPage";
import InitialPage from "@/pages/InitialPage";

const router = createBrowserRouter([
  // 비회원 접근 가능 구간
  {
    element: <Layout />,
    children: [
      {
        path: "/login",
        element: (
          <PublicRoute>
            <LoginPage />
          </PublicRoute>
        ),
        handle: { header: false, footer: false },
      },
      // {
      //   path: "/initial",
      //   element: (
      //     <PublicRoute>
      //       <InitialPage />
      //     </PublicRoute>
      //   ),
      //   handle: { header: false, footer: false },
      // },
      {
        path: "/signup",
        element: (
          <PublicRoute>
            <SignupPage />
          </PublicRoute>
        ),
        handle: { header: false, footer: false },
      },
      {
        path: "/terms",
        element: (
          <PublicRoute>
            <TermPage />
          </PublicRoute>
        ),
        handle: { header: false, footer: false },
      },
      {
        path: "/terms/:termId",
        element: (
          <PublicRoute>
            <TermDetailPage />
          </PublicRoute>
        ),
        handle: { header: false, footer: false },
      },
      {
        path: "/verification",
        element: (
          <PublicRoute>
            <PhoneVerificationPage />
          </PublicRoute>
        ),
        handle: { header: false, footer: false },
      },
    ],
  },

  // 로그인 필요 구간
  {
    path: "/",
    element: <Layout />,
    children: [
      {
        index: true,
        element: (
          <PublicRoute>
            <InitialPage />
          </PublicRoute>
        ),
        handle: { header: false, footer: false },
      },
      {
        path: "expense-map",
        element: <MapPage />,
      },
      {
        path: "challenge",
        element: <ChallengePage />,
        children: [
          {
            index: true,
            element: <AllChallenge />,
          },
          {
            path: "my",
            element: <MyChallenge />,
          },
          {
            path: "recommend",
            element: <RecommendChallenge />,
          },
        ],
      },
      {
        path: "lounge",
        children: [
          {
            index: true,
            element: <LoungeMainPage />,
          },
          { path: "start", element: <LoungeStartPage /> },
          { path: "room/:loungeId", element: <LoungeRoomPage /> },
          { path: "room/game/quiz/:loungeId/:gameId/:quizId", element: <LoungeGamePage /> },
          { path: "room/game/result/:loungeId/:gameId", element: <LoungeGameResultPage /> },
          { path: "room/game/rank/:loungeId/:gameId", element: <LoungeGameRankPage /> },
        ],
      },
      {
        path: "account-book",
        children: [
          { index: true, element: <MyAccountBookPage /> },
          {
            path: "calendar",
            children: [
              { index: true, element: <AccountBookCalendarPage /> },
              {
                path: "day/:dayId/:memberId/:transactionDate",
                element: <AccountBookCalendarDatePage />,
              },
            ],
          },
          {
            path: "statistics",
            children: [
              { index: true, element: <ExpenseStatisticsPage /> },
              {
                path: "category/:transactionDate",
                element: <CategoryStatisticsPage />,
                handle: { header: false, footer: true },
              },
              {
                path: "category/:categoryId/:transactionDate",
                element: <CategoryExpensePage />,
                handle: { header: false, footer: true },
              },
            ],
          },
          { path: "pocket-money", element: <PocketMoneyPage /> },
        ],
      },
      {
        path: "profile",
        children: [
          { index: true, element: <ProfilePage /> },
          {
            path: "friend",
            children: [
              { index: true, element: <FriendPage />, handle: { header: false, footer: true } },
              {
                path: ":memberId",
                element: <FriendCalenderPage />,
                handle: { header: false, footer: true },
              },
              {
                path: "friendinfo/:memberId",
                element: <FriendsFriendPage />,
                handle: { header: false, footer: true },
              },
            ],
          },
          { path: "room/game/result/:loungeId/:gameId", element: <LoungeGameResultPage /> },
          { path: "room/game/rank/:loungeId/:gameId", element: <LoungeGameRankPage /> },
        ],
      },
      {
        path: "asset-connection",
        element: <AssetConnectionPage />,
        handle: { header: false, footer: false },
      },
      {
        path: "setting",
        element: <SettingPage />,
        handle: { header: false },
      },

      {
        path: "*",
        element: <NotFoundPage />,
        handle: { header: false, footer: false },
      },
    ],
  },
]);

export default router;
