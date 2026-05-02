import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { Toaster } from "@/components/ui/toaster";
import { TooltipProvider } from "@/components/ui/tooltip";
import DefaultLayout from "./layouts/DefaultLayout";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import InboundList from "./pages/inbound/InboundList";
import InboundDetail from "./pages/inbound/InboundDetail";
import OutboundList from "./pages/outbound/OutboundList";
import OutboundDetail from "./pages/outbound/OutboundDetail";
import InventoryList from "./pages/inventory/InventoryList";
import InventoryCheck from "./pages/inventory/InventoryCheck";
import InventoryAlert from "./pages/inventory/InventoryAlert";
import DataSyncList from "./pages/data-sync/DataSyncList";
import DataSyncConfig from "./pages/data-sync/DataSyncConfig";
import DataSyncLog from "./pages/data-sync/DataSyncLog";
import ReportCenter from "./pages/report/ReportCenter";
import ReportDetail from "./pages/report/ReportDetail";
import AIAnalysis from "./pages/report/AIAnalysis";
import UserManage from "./pages/system/UserManage";
import RoleManage from "./pages/system/RoleManage";
import PermissionManage from "./pages/system/PermissionManage";
import SkuList from "./pages/sku/SkuList";
import SkuDetail from "./pages/sku/SkuDetail";
import SkuCategoryManage from "./pages/sku/SkuCategoryManage";
import NotFound from "./pages/NotFound";

const queryClient = new QueryClient();

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <Toaster />
      <Sonner />
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/login" element={<Login />} />
          <Route element={<DefaultLayout />}>
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/sku/list" element={<SkuList />} />
            <Route path="/sku/detail/:id?" element={<SkuDetail />} />
            <Route path="/sku/category" element={<SkuCategoryManage />} />
            <Route path="/inbound/list" element={<InboundList />} />
            <Route path="/inbound/detail/:id?" element={<InboundDetail />} />
            <Route path="/outbound/list" element={<OutboundList />} />
            <Route path="/outbound/detail/:id?" element={<OutboundDetail />} />
            <Route path="/inventory/list" element={<InventoryList />} />
            <Route path="/inventory/check" element={<InventoryCheck />} />
            <Route path="/inventory/alert" element={<InventoryAlert />} />
            <Route path="/data-sync/list" element={<DataSyncList />} />
            <Route path="/data-sync/config/:id?" element={<DataSyncConfig />} />
            <Route path="/data-sync/log" element={<DataSyncLog />} />
            <Route path="/report/center" element={<ReportCenter />} />
            <Route path="/report/detail/:type" element={<ReportDetail />} />
            <Route path="/report/ai-analysis" element={<AIAnalysis />} />
            <Route path="/system/user" element={<UserManage />} />
            <Route path="/system/role" element={<RoleManage />} />
            <Route path="/system/permission" element={<PermissionManage />} />
          </Route>
          <Route path="*" element={<NotFound />} />
        </Routes>
      </BrowserRouter>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
