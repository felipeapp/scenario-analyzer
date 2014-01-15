<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
<!--

/* Paineis de op�oes */
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
	
		<h2><ufrn:subSistema /> &gt; Submiss�o de ${atividadeExtensao.obj.registro ? 'REGISTRO':'PROPOSTA'} de A��es de Extens�o </h2>

	<h:form>
	<table id="painel_opcoes" class="formulario">
		<caption class="formulario">Selecione o tipo de A��o</caption>
		<tr>
			<td align="justify">
				<img src="${ctx}/img/extensao/programa.png"/>
				<h:commandLink action="#{programaExtensao.iniciar}"	value="Programa" />
				<p>
					Programa � entendido como o conjunto de a��es coerentes articuladas ao ensino
					e � pesquisa e integradas �s pol�ticas institucionais da Universidade direcionadas �s
					quest�es relevantes da sociedade, com car�ter regular e continuado. Um programa � 
					composto de no m�nimo 3 (tr�s) projetos e 2 (duas) outras a��es de extens�o.
				</p>
			</td>
			<td align="justify">
				<img src="${ctx}/img/extensao/projeto.png"/>
				<h:commandLink action="#{projetoExtensao.iniciar}"	value="Projeto" />
				<p>
					Projeto � definido como uma a��o processual e cont�nua de car�ter educat�vo, social, cultural, 
					cient�fico ou tecnol�gico, com objetivos espec�ficos, e que cumpram o preceito da indissociabilidade
					ensino, pesquisa e extens�o, desenvolvido de forma sistematizada e com per�odo de vig�ncia entre 3 (tr�s)
					e 12 (doze) meses. O projeto pode ser: (a) vinculado a um programa (forma preferencial); 
					(b) n�o-vinculado (projeto isolado).
				</p>
			</td>
		</tr>

		<tr>
			<td align="justify">
				<img src="${ctx}/img/extensao/curso.png"/>
				<h:commandLink action="#{cursoEventoExtensao.iniciarCurso}"	value="Curso" />
				<p>
					Curso de Extens�o � entendido como conjunto articulado de a��es pedag�gicas 
					de car�ter te�rico e/ou pr�tico, que extrapolem as cargas hor�rias curriculares 
					e que se proponham a socializar os conhecimentos produzidos na Universidade, ou fora dela, de 
					forma presencial ou � dist�ncia, vindo a contribuir para uma melhor articula��o entre o saber 
					acad�mico e as pr�ticas sociais. Os mesmos dever�o ter carga hor�ria definida e avalia��o de 
					resultados. A carga hor�ria de cada modalidade de curso � disciplinada nos termos da 
					Resolu��o 053/2008-CONSEPE, de 15/04/2008.
				</p>
			</td>
			<td align="justify">
				<img src="${ctx}/img/extensao/evento.png"/>
				<h:commandLink action="#{cursoEventoExtensao.iniciarEvento}"	value="Evento" />
				<p>
					Evento � definido como uma a��o de interesse t�cnico, social, ci�nt�fico, art�stico e esportivo:
					campanhas em geral, campeonato, ciclo de estudos, circuito, col�quio, concerto, conclave, confer�ncia,
					congresso, debate, encontro, espet�culo, exposi��o, feira, festival, f�rum, jornada, lan�amento de 
					publica��es e produtos, mesa redonda, mostra, olimp�ada, palestra, recital, semana de estudos, semin�rio,
					simp�sio, torneio, entre outras manisfesta��es, que congreguem pessoas em torno de objetivos espec�ficos.
				</p>
			</td>
		</tr>
		<tr>
			<td align="justify">
				<img src="${ctx}/img/extensao/produto.png"/>
				<h:commandLink action="#{produtoExtensao.iniciar}"	value="Produto" />
				<p>
					Produto � resultado de atividades de extens�o, ensino e pesquisa, com a finalidade de difus�o e divulga��o
					cultural, cient�fica ou tecnol�gica. � considerado produto: livros, anais, artigos, textos, revistas, manual,
					cartilhas, jornal, relat�rio, v�deos, filmes, programas de r�dio e TV, softwares, CDs, DVDs, partituras, 
					arranjos musicais, entre outros.
				</p>
			</td>
			<td align="justify">
				<%-- 
					<img src="${ctx}/img/extensao/servico.png"/>
					<a href="#"> Presta��o de Servi�o </a>
					<p>
						Realiza��o de trabalho oferecido ou contratado por terceiros (comunidade ou empresa),
						incluindo assessorias, consultorias e coopera��o interinstitucional.
					</p>
				--%>
			</td>
		</tr>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
