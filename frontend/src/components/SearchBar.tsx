import { ReactNode } from "react";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import { Search, RotateCcw } from "lucide-react";

export type SearchField =
  | { name: string; label: string; type: "input"; placeholder?: string }
  | { name: string; label: string; type: "select"; options: { label: string; value: string }[] }
  | { name: string; label: string; type: "date" };

interface Props {
  fields: SearchField[];
  values: Record<string, string>;
  onChange: (v: Record<string, string>) => void;
  onSearch: () => void;
  extra?: ReactNode;
}

export function SearchBar({ fields, values, onChange, onSearch, extra }: Props) {
  const set = (k: string, v: string) => onChange({ ...values, [k]: v });
  const reset = () => {
    const cleared = Object.fromEntries(fields.map(f => [f.name, ""]));
    onChange(cleared);
    setTimeout(onSearch, 0);
  };
  return (
    <div className="panel">
      <div className="flex flex-wrap items-end gap-3">
        {fields.map(f => (
          <div key={f.name} className="flex flex-col gap-1.5 min-w-[180px] flex-1 max-w-[240px]">
            <label className="text-xs font-medium text-muted-foreground">{f.label}</label>
            {f.type === "input" && (
              <Input
                placeholder={f.placeholder ?? `请输入${f.label}`}
                value={values[f.name] ?? ""}
                onChange={e => set(f.name, e.target.value)}
                onKeyDown={e => e.key === "Enter" && onSearch()}
              />
            )}
            {f.type === "select" && (
              <Select value={values[f.name] || "__all__"} onValueChange={v => set(f.name, v === "__all__" ? "" : v)}>
                <SelectTrigger><SelectValue placeholder={`全部${f.label}`} /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="__all__">全部</SelectItem>
                  {f.options.map(o => <SelectItem key={o.value} value={o.value}>{o.label}</SelectItem>)}
                </SelectContent>
              </Select>
            )}
            {f.type === "date" && (
              <Input type="date" value={values[f.name] ?? ""} onChange={e => set(f.name, e.target.value)} />
            )}
          </div>
        ))}
        <div className="flex gap-2">
          <Button onClick={onSearch} className="gap-1.5"><Search className="h-4 w-4" />查询</Button>
          <Button variant="outline" onClick={reset} className="gap-1.5"><RotateCcw className="h-4 w-4" />重置</Button>
          {extra}
        </div>
      </div>
    </div>
  );
}
