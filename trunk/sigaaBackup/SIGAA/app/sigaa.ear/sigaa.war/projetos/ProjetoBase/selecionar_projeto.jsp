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
	
	<h2><ufrn:subSistema /> &gt; Submiss�o de proposta de a��o acad�mica</h2>

	<h:form id="form" >
		<table id="painel_opcoes" class="formulario">
			<caption>Selecione o tipo de proposta</caption>
			<tr>
				<td align="justify">
					<img src="${ctx}/img/icones/monitoria.gif"/>
					<h:commandLink action="#{projetoBase.iniciarEnsino}"	value="Ensino" />
					<p>
						O Programa de Monitoria da ${ configSistema['siglaInstituicao'] } � uma a��o institucional direcionada � melhoria do processo de 
						ensino-�aprendizagem   dos   cursos   de   gradua��o,   envolvendo   professores   e   alunos   na   condi��o   de 
						orientadores   e   monitores,   respectivamente,   efetivado   por   meio   de   projetos   de   ensino (Art. 2� da Res. 013/2006 - CONSEPE)
					</p>
				</td>
				<td align="justify">
					<img src="${ctx}/img/icones/potion_red.png"/>
					<h:commandLink action="#{projetoBase.iniciarPesquisa}"	value="Pesquisa" >
						<f:param name="linkPesquisa" value="/pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=popular&interno=true" />
					</h:commandLink>
					<p>
						As   atividades   relacionadas   �   Pesquisa   no   �mbito   da   ${ configSistema['siglaInstituicao'] }   compreendem   o   Programa   de   Inicia��o 
						Cient�fica, o diret�rio das Bases de Pesquisa e os Projetos de Pesquisa, sendo a Pr�reitoria de Pesquisa, 
						PROPESQ, a respons�vel pelo gerenciamento ou supervis�o de  a��es globais na �rea da pesquisa, como 
						o programa de inicia��o cient�fica (bolsas e congressos), os projetos de infra�estrutura em Pesquisa e o 
						cadastramento e acompanhamento dos projetos e das bases de Pesquisa (Grupos de Pesquisa).
					</p>
				</td>
			</tr>
	
			<tr>
				<td align="justify">
					<img src="${ctx}/img/icones/houses.png"/>
					<h:commandLink action="#{projetoBase.iniciarExtensao}"	value="Extens�o" />
					<p>
					    Uma   atividade   de   extens�o   pode   ser   considerada   como   (a)   uma   atividade   formadora,   vinculada   ao 
						processo pedag�gico do aluno; (b) coordenada por docente e (c) com contato direto com a sociedade.
	    				As   atividades   de   Extens�o   s�o   aquelas   que   envolvem   professores,   alunos   e   servidores   t�cnicos�
						administrativos e que se enquadrem nas modalidades: Programas, Projetos, Cursos, Eventos, Produtos e Presta��o de servi�os.
					</p>
				</td>
				<td align="justify">
					<img src="${ctx}/img/projetos/associado.png"/>
					<h:commandLink action="#{projetoBase.selecionarTipoProjetoIntegrado}"	value="A��es Acad�micas Integradas" />
					<p>
						Corresponde a uma proposta de a��o acad�mica que envolve mais de um campo de atua��o da universidade. Ou seja, um projeto �nico que
						integra atividades de ensino, pesquisa e extens�o.
					</p>
				</td>
			</tr>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>