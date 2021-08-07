import  java.io.* ;
import java.util.*;
import java.sql.*;
import java.math.*;

public class teampj {
    public static void main(String[] args) throws SQLException
    {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

        try
        {
            Scanner scan = new Scanner(System.in);
            String inp; //사용자 입력받을 문자열
            String choice;

            // JDBC를 이용해 PostgreSQL 서버 및 데이터베이스 연결

            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/","postgres", "zxcvbnm147");
            st = conn.createStatement();

            st.execute("Create table Restaurant(RID int, Type varchar(20), RName varchar(40),  Addr1 varchar(40), Addr2 varchar(40));;");
            st.execute("COPY Restaurant FROM 'C:\\Users\\jghga\\youngsangu.csv' WITH CSV Encoding 'euc-kr';");
            st.execute("Create table Rating(RID int, Taste int, Cleanness int, Response int, Rating_Ave float);");
            st.execute("Create table Waiting(RID int, Waiting_Num int, Waiting_Time int, Total_Num int);");

            int c1 = 0, c2 = 0, c3 = 0;
            float c4 = 0;

            for( int i = 0; i < 110; i++){

                c1 = (int)((Math.random()*10000)%5);
                c2 = (int)((Math.random()*10000)%5);
                c3 = (int)((Math.random()*10000)%5);
                c4 = (float)(c1+c2+c3)/3;

                st.execute("insert into Rating values(" + i + ", " + c1 + ", " + c2 + ", " + c3 + ", " + c4 + ");");
            }

            for( int i = 0; i < 110; i++){
                c1 = (int)((Math.random()*10000)%15);
                c2 = (int)((Math.random()*10000)%80);
                c3 = (int)((Math.random()*10000)%50);

                st.execute("insert into Waiting values(" + i + ", " + c1 + ", " + c2 + ", " + c3 + ");");
            }

            System.out.print("--------------------------------------------------\n");
            System.out.print("|           Waiting for JMT in Youngsan           |\n");
            System.out.print("--------------------------------------------------\n\n");
            System.out.print("--------------☆현재 평점 3 이상인 음식점☆--------------\n");

            //****평점 3이상인 음식점과 위치 뽑아내는쿼리

            rs = st.executeQuery("select Rname, Addr1, Addr2, rating_ave from Restaurant natural join Rating where Rating_Ave >= 3;");
            System.out.println("음식점 이름                         주소                                                             평점");
            System.out.println("----------------------------------------------------------------------------------------------------");

            while(rs.next()) {
                String Rname = rs.getString(1);
                String Addr1 = rs.getString(2);
                String Addr2 = rs.getString(3);
                float Rating_Ave = rs.getFloat(4);

                if(Addr2 == null) {Addr2 = "\t";}
                System.out.printf("%-24s\t%-26s\t%-24s\t%s\n", Rname, Addr1, Addr2, String.format("%.2f", Rating_Ave));
            }

            st.execute("create view Name as\r\n"
                    + "select RName\r\n"
                    + "from Restaurant\r\n");

            String namelist;

            main :

            while(true) {

                System.out.print("어떤 음식점을 검색할까요? ('exit'를 입력하시면 종료됩니다)\n: ");

                Loop1 :

                while(true) {
                    inp = scan.nextLine();

                    if (inp.equalsIgnoreCase("exit")){
                        System.out.print("앱을 종료합니다.\n ");
                        System.exit(0);
                    }

                    else {
                        rs = st.executeQuery("select * from Name;");

                        while(rs.next()) { //이름이 존재하면 종료
                            namelist = rs.getString(1);

                            if (namelist.equals(inp)) {
                                break Loop1;

                            }
                        }
                        System.out.print("일치하는 음식점이 없습니다. 다시 검색해주세요.\n: ");
                    }
                }

                L1 :

                while(true) {
                    System.out.print("'"+inp+"' 음식점의 위치를 확인하려면 1, 평점을 확인하려면 2, 웨이팅 현황을 확인하려면 3를 입력해주세요." + "\n: ");
                    choice = scan.nextLine();

                    switch(choice) {

                        case "1":

                            //****음식점 위치 찾는 쿼리

                            rs = st.executeQuery("select Rname, Addr1, Addr2 from Restaurant where Rname = '" + namelist + "' ;");
                            System.out.println("\t음식점: \t주소");
                            System.out.println("-------------------------------------------------");

                            while(rs.next()) {
                                String Rname = rs.getString(1);
                                String Addr1 = rs.getString(2);
                                String Addr2 = rs.getString(3);
                                if(Addr2 == null) {Addr2 = " ";}
                                System.out.printf("\t%s: \t%s\t%s\n", Rname, Addr1, Addr2);

                            }
                            break;

                        case "2" :

                            //****리뷰점수와 평점 뽑아내는 쿼리

                            rs = st.executeQuery("select Rname, Taste, Cleanness, Response, Rating_Ave from Restaurant natural join Rating where RName = '" + namelist + "' ;");
                            System.out.println("\t음식점: \t맛\t청결도\t서비스\t평균 평점");
                            System.out.println("-------------------------------------------------");

                            while(rs.next()) {

                                String Rname = rs.getString(1);
                                int Taste = rs.getInt(2);
                                int Cleanness = rs.getInt(3);
                                int Response = rs.getInt(4);
                                float Rating_Ave = rs.getFloat(5);

                                System.out.printf("\t%s\t%d\t%d\t%d\t%s\n", Rname, Taste, Cleanness, Response, String.format("%.2f", Rating_Ave));
                            }
                            break;

                        case "3" :

                            //****웨이팅 정보 뽑아내는 쿼리

                            rs = st.executeQuery("select RID, Rname, Waiting_Num, Waiting_Time, Total_Num from Restaurant natural join Waiting where RName = '" + namelist + "';");
                            System.out.println("\t음식점\t현재 대기팀\t대기 시간\t누적 방문팀 수");

                            System.out.println("-------------------------------------------------");

                            int RID = 0;
                            String Rname = null;
                            int Waiting_Num = 0;
                            int Waiting_Time = 0;
                            int Total_Num = 0;

                            while(rs.next()) {
                                RID = rs.getInt(1);
                                Rname = rs.getString(2);
                                Waiting_Num = rs.getInt(3);
                                Waiting_Time = rs.getInt(4);
                                Total_Num = rs.getInt(5);

                                System.out.printf("\t%s\t%d\t%d\t%d\n", Rname, Waiting_Num, Waiting_Time, Total_Num);
                            }
                            System.out.print("\n\n");
                            System.out.print("이 음식점에 예약하시려면 1을 눌러주세요.\n");
                            choice = scan.nextLine();

                            if(choice.equals("1")) {

                                //****웨이팅 테이블에 +1하는 쿼리
                                //RID int, Waiting_Num int, Waiting_Time int, Total_Num int
                                Waiting_Num += 1;
                                Waiting_Time += 10;
                                Total_Num += 1;

                                st.execute("UPDATE Waiting SET Waiting_Num = '"+ Waiting_Num +"', Waiting_time = '"+ Waiting_Time +"', Total_Num = '" + Total_Num + "'  WHERE RID = '" + RID + "';");
                                System.out.printf("%s: 예약이 완료되었습니다.", namelist);
                                System.out.println();

                                rs = st.executeQuery("select Rname, Waiting_Num, Waiting_Time, Total_Num from Restaurant natural join Waiting where RName = '" + namelist + "';");
                                System.out.println("\t음식점\t현재 대기팀\t대기 시간\t누적 방문팀 수");
                                System.out.println("-------------------------------------------------");

                                while(rs.next()) {
                                    Rname = rs.getString(1);
                                    Waiting_Num = rs.getInt(2);
                                    Waiting_Time = rs.getInt(3);
                                    Total_Num = rs.getInt(4);
                                    System.out.printf("\t%s\t%d\t%d\t%d\n", Rname, Waiting_Num, Waiting_Time, Total_Num);

                                }
                            }
                            break;
                    }
                    System.out.print("\n\n");
                    System.out.print("해당 음식점 정보로 돌아가려면 1, 다른 음식점을 검색하시려면 2를 입력해주세요." + "\n: ");

                    choice = scan.nextLine();

                    if(choice.equals("1")) {
                        continue;

                    }

                    else if(choice.equals("2")) {
                        break L1;
                    }

                    else {
                        System.out.print("앱을 종료합니다.\n ");
                        System.exit(0);

                    }
                }
            }
        } catch(SQLException ex)

        {
            throw ex;
        }
    }
}