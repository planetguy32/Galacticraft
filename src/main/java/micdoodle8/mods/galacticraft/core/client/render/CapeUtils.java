package micdoodle8.mods.galacticraft.core.client.render;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.core.util.VersionUtil;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

public class CapeUtils
{
    @SideOnly(Side.CLIENT)
    public static class ImageBufferDownloadGC implements IImageBuffer
    {
        private int imageWidth;
        private int imageHeight;

        public BufferedImage parseUserSkin(BufferedImage p_78432_1_)
        {
            if (p_78432_1_ == null)
            {
                return null;
            }
            else
            {
                this.imageWidth = 512;
                this.imageHeight = 256;
                BufferedImage bufferedimage1 = new BufferedImage(this.imageWidth, this.imageHeight, 2);
                Graphics graphics = bufferedimage1.getGraphics();
                graphics.drawImage(p_78432_1_, 0, 0, null);
                graphics.dispose();
                return bufferedimage1;
            }
        }

        public void func_152634_a() {}
    }

    @SideOnly(Side.CLIENT)
    public static class ThreadDownloadImageDataGC extends SimpleTexture
    {
        private static final Logger logger = LogManager.getLogger();
        private static final AtomicInteger threadDownloadCounter = new AtomicInteger(0);
        private final File field_152434_e;
        private final String imageUrl;
        private final IImageBuffer imageBuffer;
        private BufferedImage bufferedImage;
        private Thread imageThread;
        private boolean textureUploaded;

        public ThreadDownloadImageDataGC(File p_i1049_1_, String p_i1049_2_, ResourceLocation p_i1049_3_, IImageBuffer p_i1049_4_)
        {
            super(p_i1049_3_);
            this.field_152434_e = p_i1049_1_;
            this.imageUrl = p_i1049_2_;
            this.imageBuffer = p_i1049_4_;
        }

        private void checkTextureUploaded()
        {
            if (!this.textureUploaded)
            {
                if (this.bufferedImage != null)
                {
                    if (this.textureLocation != null)
                    {
                        this.deleteGlTexture();
                    }

                    TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
                    this.textureUploaded = true;
                }
            }
        }

        public int getGlTextureId()
        {
            this.checkTextureUploaded();
            return super.getGlTextureId();
        }

        public void setBufferedImage(BufferedImage p_147641_1_)
        {
            this.bufferedImage = p_147641_1_;
        }

        public void loadTexture(IResourceManager p_110551_1_) throws IOException
        {
            if (this.bufferedImage == null && this.textureLocation != null)
            {
                super.loadTexture(p_110551_1_);
            }

            if (this.imageThread == null)
            {
                if (this.field_152434_e != null && this.field_152434_e.isFile())
                {
                    FMLLog.fine("Loading http texture from local cache (%s)", this.field_152434_e);

                    try
                    {
                        this.bufferedImage = ImageIO.read(this.field_152434_e);

                        if (this.imageBuffer != null)
                        {
                            this.setBufferedImage(this.imageBuffer.parseUserSkin(this.bufferedImage));
                        }
                    }
                    catch (IOException ioexception)
                    {
                        logger.error("Couldn\'t load skin " + this.field_152434_e, ioexception);
                        this.func_152433_a();
                    }
                }
                else
                {
                    this.func_152433_a();
                }
            }
        }

        protected void func_152433_a()
        {
            this.imageThread = new Thread("Texture Downloader #" + threadDownloadCounter.incrementAndGet())
            {
                public void run()
                {
                    HttpURLConnection httpurlconnection = null;
                    FMLLog.fine("Downloading http texture from %s to %s", ThreadDownloadImageDataGC.this.imageUrl, ThreadDownloadImageDataGC.this.field_152434_e);

                    try
                    {
                        httpurlconnection = (HttpURLConnection)(new URL(ThreadDownloadImageDataGC.this.imageUrl)).openConnection();
                        httpurlconnection.setDoInput(true);
                        httpurlconnection.setDoOutput(false);
                        httpurlconnection.connect();

                        if (httpurlconnection.getResponseCode() / 100 == 2)
                        {
                            BufferedImage bufferedimage;

                            if (ThreadDownloadImageDataGC.this.field_152434_e != null)
                            {
                                FileUtils.copyInputStreamToFile(httpurlconnection.getInputStream(), ThreadDownloadImageDataGC.this.field_152434_e);
                                bufferedimage = ImageIO.read(ThreadDownloadImageDataGC.this.field_152434_e);
                            }
                            else
                            {
                                bufferedimage = ImageIO.read(httpurlconnection.getInputStream());
                            }

                            if (ThreadDownloadImageDataGC.this.imageBuffer != null)
                            {
                                bufferedimage = ThreadDownloadImageDataGC.this.imageBuffer.parseUserSkin(bufferedimage);
                            }

                            ThreadDownloadImageDataGC.this.setBufferedImage(bufferedimage);
                            return;
                        }
                    }
                    catch (Exception exception)
                    {
                        ThreadDownloadImageDataGC.logger.error("Couldn\'t download http texture", exception);
                        return;
                    }
                    finally
                    {
                        if (httpurlconnection != null)
                        {
                            httpurlconnection.disconnect();
                        }
                    }
                }
            };
            this.imageThread.setDaemon(true);
            this.imageThread.start();
        }
    }
}