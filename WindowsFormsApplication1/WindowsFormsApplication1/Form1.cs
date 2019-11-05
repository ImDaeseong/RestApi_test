using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using Newtonsoft.Json;
using System.Net.Http;
using System.Threading;
using System.Threading.Tasks;
using System.IO;

namespace WindowsFormsApplication1
{
    public partial class Form1 : Form
    {
        string sPath = string.Format("{0}\\package.json", Application.StartupPath);

        private List<Game> gameList = null;

        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            LoadJson();

            for (int i = 1; i <= 6; i++)
            {
                comboBox1.Items.Add(i);
            }

            gameList = new List<Game>();                            
        }       
        
        //전체 항목 가져오기
        private void button1_Click(object sender, EventArgs e)
        {
            textBox1.Text = "";
            getAllList();
        }

        //한개 항목 가져오기
        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (comboBox1.SelectedIndex == -1) return;

            textBox1.Text = "";
            string sItem = comboBox1.SelectedItem.ToString();
            getItemList(sItem);
        }

        //파일 쓰기
        private void button2_Click(object sender, EventArgs e)
        {
            setjson();
        }


        //json 파일 쓰기
        private void setjson()
        {
            for (int i = 0; i < 10; i++)
            {
                string sM = string.Format("item:{0}", i);

                Game item = new Game();
                item.ID = sM;
                item.GameTitle = sM;
                item.PackageName = sM;

                item.GameDesc = new GameDesc();
                item.GameDesc.Details1 = sM;
                item.GameDesc.Details2 = sM;
                
                gameList.Add(item);
                File.WriteAllText(sPath, JsonConvert.SerializeObject(gameList));
            }
        }

        //json 파일 읽기
        private void LoadJson()
        {
            if (File.Exists(sPath))
            {
                textBox1.Text = "";
                using (StreamReader reader = new StreamReader(sPath))
                {
                    string json = reader.ReadToEnd();
                    List<Game> item = JsonConvert.DeserializeObject<List<Game>>(json);
                    foreach (Game game in item)
                    {
                        string sResult = string.Format("GameTitle:{0} PackageName:{1} ID:{2}\r\nDetails1:{3} Details2:{4}\r\n", game.GameTitle, game.PackageName, game.ID, game.GameDesc.Details1, game.GameDesc.Details2);
                        textBox1.Text += sResult;
                    }
                }

            }
        }

        //json 전체 목록 가져오기
        private async void getAllList()
        {
            string defaulturl = "http://127.0.0.1:8080/api/AllList";
            string sUrl = string.Format("{0}", defaulturl);

            using (var client = new HttpClient())
            {
                var resp = await client.GetStringAsync(sUrl);

                List<Game> item = JsonConvert.DeserializeObject<List<Game>>(resp);
                for (int i = 0; i < item.Count; i++)
                {
                    string sResult = string.Format("GameTitle:{0} PackageName:{1} ID:{2}\r\nDetails1:{3} Details2:{4}\r\n", item[i].GameTitle, item[i].PackageName, item[i].ID, item[i].GameDesc.Details1, item[i].GameDesc.Details2);
                    textBox1.Text += sResult;
                }
            }
        }

        //json 특정 항목 가져오기
        private async void getItemList(string nSelect)
        {
            string sUrl = string.Format("http://127.0.0.1:8080/api/item/{0}", nSelect);

            using (var client = new HttpClient())
            {
                var resp = await client.GetStringAsync(sUrl);

                Game item = JsonConvert.DeserializeObject<Game>(resp);

                string sResult = string.Format("GameTitle:{0} PackageName:{1} ID:{2}\r\nDetails1:{3} Details2:{4}\r\n", item.GameTitle, item.PackageName, item.ID, item.GameDesc.Details1, item.GameDesc.Details2);
                textBox1.Text += sResult;
            }
        }               

    }


    public class Game
    {
        public string ID { get; set; }

        public string PackageName { get; set; }

        public string GameTitle { get; set; }

        public GameDesc GameDesc { get; set; }
       
    }

    public class GameDesc
    {
        public string Details1 { get; set; }

        public string Details2 { get; set; }
    }
    
}
