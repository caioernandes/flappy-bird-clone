package com.caioernandes.fb;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Random numeroRandomico;
    private BitmapFont fonte;
    private Circle passaroCirculo;
    private Rectangle canoTopoRectangle;
    private Rectangle canoBaixoRectangle;
    private Texture gameOver;
    private BitmapFont mensagem;
    //private ShapeRenderer shape;

    //Atributos de configuracao
    private float LarguraDispositivo;
    private float    AlturaDispositivo;
    private int estadoJogo = 0; //0->jogo nao iniciado 1-> jogo iniciado 2-> jogo game over
    private int pontuacao = 0;

    private float variacao = 0;
    private float velocidadeQueda = 0;
    private float posicaoInicialVertical;
    private float posicaoMovimentoCanoHorizontal;
    private float espacoEntreCanos;
    private float deltaTime;
    private int alturaEntreCanosRandomica;
    private boolean marcouPonto;

    //camera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;

	@Override
	public void create () {
	    batch = new SpriteBatch();

	    fonte = new BitmapFont();
	    fonte.setColor(Color.WHITE);
	    fonte.getData().setScale(6);

	    mensagem = new BitmapFont();
	    mensagem.setColor(Color.WHITE);
	    mensagem.getData().setScale(3);

        //shape = new ShapeRenderer();

	    passaroCirculo = new Circle();
        /*canoTopoRectangle = new Rectangle();
	    canoBaixoRectangle = new Rectangle();*/

        passaros = new Texture[3];
	    passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

	    fundo = new Texture("fundo.png");
	    canoBaixo = new Texture("cano_baixo.png");
	    canoTopo = new Texture("cano_topo.png");
	    gameOver = new Texture("game_over.png");

	    //Configuracao da camera
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

	    LarguraDispositivo = VIRTUAL_WIDTH;
        AlturaDispositivo = VIRTUAL_HEIGHT;
        posicaoInicialVertical = AlturaDispositivo / 2;
        posicaoMovimentoCanoHorizontal = LarguraDispositivo;
        espacoEntreCanos = 300;
        numeroRandomico = new Random();
	}

	@Override
	public void render () {

	    camera.update();

	    //Limpar frames anteriores
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltaTime = Gdx.graphics.getDeltaTime();
        variacao += deltaTime * 10;

        if (variacao > 3) variacao = 0;

	    if (estadoJogo == 0) { //nao iniciado
	        if (Gdx.input.justTouched()) estadoJogo = 1;
        } else { //iniciado

            velocidadeQueda ++;
            if (posicaoInicialVertical > 0)
                posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;

            if (estadoJogo == 1) {
                posicaoMovimentoCanoHorizontal -= deltaTime * 400;

                //evento de fazer o passaro voar
                if (Gdx.input.justTouched()) {
                    velocidadeQueda = -16;
                }

                //verifica se o cano saiu inteiramente da tela
                if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
                    posicaoMovimentoCanoHorizontal = LarguraDispositivo - 100;
                    alturaEntreCanosRandomica = numeroRandomico.nextInt(400) - 200;
                    marcouPonto = false;
                }

                //verifica pontuacao
                if (posicaoMovimentoCanoHorizontal < 120) {
                    if (!marcouPonto){
                        pontuacao++;
                        marcouPonto = true;
                    }
                }
            } else { //game over estado -> 2
                if (Gdx.input.justTouched()) {
                    estadoJogo = 0;
                    pontuacao = 0;
                    velocidadeQueda = 0;
                    posicaoInicialVertical = AlturaDispositivo / 2;
                    posicaoMovimentoCanoHorizontal = LarguraDispositivo;
                }
            }

        }

        //Configurar dados de projecao da camera
        batch.setProjectionMatrix(camera.combined);

	    batch.begin();

	    batch.draw(fundo, 0, 0, LarguraDispositivo, AlturaDispositivo);
	    batch.draw(canoTopo, posicaoMovimentoCanoHorizontal,AlturaDispositivo / 2 + espacoEntreCanos
                / 2 + alturaEntreCanosRandomica);
	    batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal,AlturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica);
	    batch.draw(passaros[(int)variacao], 120, posicaoInicialVertical);
	    fonte.draw(batch, String.valueOf(pontuacao), LarguraDispositivo / 2, AlturaDispositivo - 50);

	    if (estadoJogo == 2) {
            batch.draw(gameOver, LarguraDispositivo / 2 - gameOver.getWidth() / 2, AlturaDispositivo / 2);
            mensagem.draw(batch,"Toque para reiniciar",LarguraDispositivo / 2 - gameOver.getWidth() / 2, AlturaDispositivo / 2 - gameOver.getHeight() / 2);
        }

	    batch.end();

        passaroCirculo.set(120 + passaros[0].getWidth() / 2, posicaoInicialVertical + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);
        canoBaixoRectangle = new Rectangle(
                posicaoMovimentoCanoHorizontal, AlturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica,
                canoBaixo.getWidth(), canoBaixo.getHeight()
        );
        canoTopoRectangle = new Rectangle(
                posicaoMovimentoCanoHorizontal, AlturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica,
                canoTopo.getWidth(), canoTopo.getHeight()
        );

	    //Desenhar formas
        /*shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
        shape.rect(canoBaixoRectangle.x, canoBaixoRectangle.y, canoBaixoRectangle.width, canoBaixoRectangle.height);
        shape.rect(canoTopoRectangle.x, canoTopoRectangle.y, canoTopoRectangle.width, canoTopoRectangle.height);
        shape.setColor(Color.RED);

        shape.end();*/

        //Teste de colisao
        if (Intersector.overlaps(passaroCirculo, canoBaixoRectangle) ||
                Intersector.overlaps(passaroCirculo, canoTopoRectangle) ||
                posicaoInicialVertical <= 0 || posicaoInicialVertical >= AlturaDispositivo) {
            estadoJogo = 2;
        }
	}

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
    }
}