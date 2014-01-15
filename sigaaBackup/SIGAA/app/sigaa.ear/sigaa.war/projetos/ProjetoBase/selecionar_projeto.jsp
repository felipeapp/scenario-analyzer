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
	
	<h2><ufrn:subSistema /> &gt; Submissão de proposta de ação acadêmica</h2>

	<h:form id="form" >
		<table id="painel_opcoes" class="formulario">
			<caption>Selecione o tipo de proposta</caption>
			<tr>
				<td align="justify">
					<img src="${ctx}/img/icones/monitoria.gif"/>
					<h:commandLink action="#{projetoBase.iniciarEnsino}"	value="Ensino" />
					<p>
						O Programa de Monitoria da ${ configSistema['siglaInstituicao'] } é uma ação institucional direcionada à melhoria do processo de 
						ensino-­aprendizagem   dos   cursos   de   graduação,   envolvendo   professores   e   alunos   na   condição   de 
						orientadores   e   monitores,   respectivamente,   efetivado   por   meio   de   projetos   de   ensino (Art. 2º da Res. 013/2006 - CONSEPE)
					</p>
				</td>
				<td align="justify">
					<img src="${ctx}/img/icones/potion_red.png"/>
					<h:commandLink action="#{projetoBase.iniciarPesquisa}"	value="Pesquisa" >
						<f:param name="linkPesquisa" value="/pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=popular&interno=true" />
					</h:commandLink>
					<p>
						As   atividades   relacionadas   à   Pesquisa   no   âmbito   da   ${ configSistema['siglaInstituicao'] }   compreendem   o   Programa   de   Iniciação 
						Científica, o diretório das Bases de Pesquisa e os Projetos de Pesquisa, sendo a Pró­reitoria de Pesquisa, 
						PROPESQ, a responsável pelo gerenciamento ou supervisão de  ações globais na área da pesquisa, como 
						o programa de iniciação científica (bolsas e congressos), os projetos de infra­estrutura em Pesquisa e o 
						cadastramento e acompanhamento dos projetos e das bases de Pesquisa (Grupos de Pesquisa).
					</p>
				</td>
			</tr>
	
			<tr>
				<td align="justify">
					<img src="${ctx}/img/icones/houses.png"/>
					<h:commandLink action="#{projetoBase.iniciarExtensao}"	value="Extensão" />
					<p>
					    Uma   atividade   de   extensão   pode   ser   considerada   como   (a)   uma   atividade   formadora,   vinculada   ao 
						processo pedagógico do aluno; (b) coordenada por docente e (c) com contato direto com a sociedade.
	    				As   atividades   de   Extensão   são   aquelas   que   envolvem   professores,   alunos   e   servidores   técnicos­
						administrativos e que se enquadrem nas modalidades: Programas, Projetos, Cursos, Eventos, Produtos e Prestação de serviços.
					</p>
				</td>
				<td align="justify">
					<img src="${ctx}/img/projetos/associado.png"/>
					<h:commandLink action="#{projetoBase.selecionarTipoProjetoIntegrado}"	value="Ações Acadêmicas Integradas" />
					<p>
						Corresponde a uma proposta de ação acadêmica que envolve mais de um campo de atuação da universidade. Ou seja, um projeto único que
						integra atividades de ensino, pesquisa e extensão.
					</p>
				</td>
			</tr>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>