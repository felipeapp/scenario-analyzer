<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
<!--

/* Paineis de opçoes */
#painel_opcoes {
	width: 90%;
}

#painel_opcoes td {
	padding: 3px;
	width: 50%;
	vertical-align: top;
}

#painel_opcoes img {
	display: block;
	margin: 0 auto;
}

#painel_opcoes a {
	background: #EFF3FA;
	line-height: 1.25em;
	border-bottom: 1px solid #CCC;
	font-size: 1.1em;
	font-variant: small-caps;
	display: block;
	padding: 2px 0;
	text-align: center
}

#painel_opcoes a:hover {
	color: #D59030;
}

#painel_opcoes p {
	padding: 3px 6px;
	text-indent: 2em;
}

-->
</style>


<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	
		<h2><ufrn:subSistema /> &gt; Submissão de ${atividadeExtensao.obj.registro ? 'REGISTRO':'PROPOSTA'} de Ações de Extensão </h2>

	<h:form>
	<table id="painel_opcoes" class="formulario">
		<caption class="formulario">Selecione o tipo de Ação</caption>
		<tr>
			<td align="justify">
				<img src="${ctx}/img/extensao/programa.png"/>
				<h:commandLink action="#{programaExtensao.iniciar}"	value="Programa" />
				<p>
					Programa é entendido como o conjunto de ações coerentes articuladas ao ensino
					e à pesquisa e integradas às políticas institucionais da Universidade direcionadas às
					questões relevantes da sociedade, com caráter regular e continuado. Um programa é 
					composto de no mínimo 3 (três) projetos e 2 (duas) outras ações de extensão.
				</p>
			</td>
			<td align="justify">
				<img src="${ctx}/img/extensao/projeto.png"/>
				<h:commandLink action="#{projetoExtensao.iniciar}"	value="Projeto" />
				<p>
					Projeto é definido como uma ação processual e contínua de caráter educatívo, social, cultural, 
					científico ou tecnológico, com objetivos específicos, e que cumpram o preceito da indissociabilidade
					ensino, pesquisa e extensão, desenvolvido de forma sistematizada e com período de vigência entre 3 (três)
					e 12 (doze) meses. O projeto pode ser: (a) vinculado a um programa (forma preferencial); 
					(b) não-vinculado (projeto isolado).
				</p>
			</td>
		</tr>

		<tr>
			<td align="justify">
				<img src="${ctx}/img/extensao/curso.png"/>
				<h:commandLink action="#{cursoEventoExtensao.iniciarCurso}"	value="Curso" />
				<p>
					Curso de Extensão é entendido como conjunto articulado de ações pedagógicas 
					de caráter teórico e/ou prático, que extrapolem as cargas horárias curriculares 
					e que se proponham a socializar os conhecimentos produzidos na Universidade, ou fora dela, de 
					forma presencial ou à distância, vindo a contribuir para uma melhor articulação entre o saber 
					acadêmico e as práticas sociais. Os mesmos deverão ter carga horária definida e avaliação de 
					resultados. A carga horária de cada modalidade de curso é disciplinada nos termos da 
					Resolução 053/2008-CONSEPE, de 15/04/2008.
				</p>
			</td>
			<td align="justify">
				<img src="${ctx}/img/extensao/evento.png"/>
				<h:commandLink action="#{cursoEventoExtensao.iniciarEvento}"	value="Evento" />
				<p>
					Evento é definido como uma ação de interesse técnico, social, ciêntífico, artístico e esportivo:
					campanhas em geral, campeonato, ciclo de estudos, circuito, colóquio, concerto, conclave, conferência,
					congresso, debate, encontro, espetáculo, exposição, feira, festival, fórum, jornada, lançamento de 
					publicações e produtos, mesa redonda, mostra, olimpíada, palestra, recital, semana de estudos, seminário,
					simpósio, torneio, entre outras manisfestações, que congreguem pessoas em torno de objetivos específicos.
				</p>
			</td>
		</tr>
		<tr>
			<td align="justify">
				<img src="${ctx}/img/extensao/produto.png"/>
				<h:commandLink action="#{produtoExtensao.iniciar}"	value="Produto" />
				<p>
					Produto é resultado de atividades de extensão, ensino e pesquisa, com a finalidade de difusão e divulgação
					cultural, científica ou tecnológica. É considerado produto: livros, anais, artigos, textos, revistas, manual,
					cartilhas, jornal, relatório, vídeos, filmes, programas de rádio e TV, softwares, CDs, DVDs, partituras, 
					arranjos musicais, entre outros.
				</p>
			</td>
			<td align="justify">
				<%-- 
					<img src="${ctx}/img/extensao/servico.png"/>
					<a href="#"> Prestação de Serviço </a>
					<p>
						Realização de trabalho oferecido ou contratado por terceiros (comunidade ou empresa),
						incluindo assessorias, consultorias e cooperação interinstitucional.
					</p>
				--%>
			</td>
		</tr>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
