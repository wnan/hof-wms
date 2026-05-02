import { Button } from "@/components/ui/button";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { Download, FileSpreadsheet, FileText } from "lucide-react";
import { toast } from "sonner";

interface Props {
  onExport?: (format: "excel" | "pdf") => void | Promise<void>;
}

export function ExportButton({ onExport }: Props) {
  const handle = async (format: "excel" | "pdf") => {
    try {
      if (onExport) await onExport(format);
      else toast.success(`已生成 ${format.toUpperCase()} 导出任务`);
    } catch {
      toast.error("导出失败");
    }
  };
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="outline" className="gap-1.5"><Download className="h-4 w-4" />导出</Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        <DropdownMenuItem onClick={() => handle("excel")}><FileSpreadsheet className="h-4 w-4 mr-2" />导出 Excel</DropdownMenuItem>
        <DropdownMenuItem onClick={() => handle("pdf")}><FileText className="h-4 w-4 mr-2" />导出 PDF</DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
