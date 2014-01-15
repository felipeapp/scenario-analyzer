<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
<!--

/* Paineis de op�oes */
#painel_opcoes {
	width: 95%;
}

#painel_opcoes td {
	padding: 5px;
	width: 50%;
	height: 200px;
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
					<img src="${ctx}/img/projetos/document_new.png"/>
					<h:commandLink action="#{projetoBase.iniciarProjetoInternoComFinanciamentoInterno}"
						value="Projeto Com Financiamento Interno (Edital)" id="lnkIniciarProjetoInternoComFinanciamentoInterno"/>
					<p>
						Escolha esta op��o caso deseje cadastrar um projeto acad�mico com apoio financeiro	
						de algum Edital Interno a ${ configSistema['siglaInstituicao'] }. Nestes casos, o projeto dever� seguir os seguintes passos:
							<ol>
								<li>Cadastrar Projeto Acad�mico selecionando um edital espec�fico (esta opera��o em quest�o).</li>
								<li>Avalia��o do projeto conforme normas do edital selecionado.</li>
							</ol>
						O projeto ser� submetido �s regras determinadas no edital e dever� entrar em execu��o conforme publica��o do seu resultado.
					</p>
				</td>			
			
				<td align="justify">
					<img src="${ctx}/img/projetos/document.png"/>
					<h:commandLink action="#{projetoBase.iniciarProjetoInternoSemFinanciamentoInterno}"	
						value="Projeto Sem Financiamento Interno" id="lnkIniciarProjetoInternoSemFinanciamentoInterno"/>
					<p>
						Escolha esta op��o caso deseje cadastrar um projeto acad�mico que N�O necessita de apoio financeiro da ${ configSistema['siglaInstituicao'] }. 
						Nestes casos, o projeto dever� seguir os seguintes passos:
							<ol>
								<li>Cadastrar Projeto Acad�mico (esta opera��o em quest�o).</li>
								<li>Avalia��o do projeto pelo Comit� de Ensino, Pesquisa e Extens�o.</li>
							</ol>
						O projeto dever� entrar em execu��o conforme o resultado das avalia��es realizadas pelo Comit�.
					</p>
				</td>			
			</tr>
			
			<tr>						
				<td align="justify">
					<img src="${ctx}/img/projetos/document_ok.png"/>
					<h:commandLink action="#{projetoBase.iniciarProjetoExternoPesquisadorIndividual}"	
						value=" Projeto j� Aprovado em Editais de Pesquisador Individual" 
						id="lnkIniciarProjetoExternoPesquisadorIndividual"/>
					<p>
						Escolha esta op��o caso deseje cadastrar projetos que foram aprovados junto ao CNPQ, FAPERN, etc. A 
						rela��o � entre o �rg�o financiador e o pesquisador. N�o h� recebimento de recursos nem execu��o na ${ configSistema['siglaInstituicao'] }.<br/>
						Nestes casos, o seu projeto j� ser� considerado EM EXECU��O.
					</p>
				</td>
				
				<td align="justify">
					<img src="${ctx}/img/projetos/document_into.png"/>
					<h:commandLink action="#{projetoBase.iniciarProjetoExternoConvenio}"	
						value="Projeto para Tramita��o de Instrumento Jur�dico na #{ configSistema['siglaInstituicao'] }" 
						id="lnkIniciarProjetoExternoConvenio"/>
					<p>
						Escolha esta op��o caso deseje cadastrar um projeto acad�mico que ser� base para um conv�nio, 
						contrato, termo de coopera��o ou outro instrumento.  Nestes casos, o projeto dever� seguir os 
						seguintes passos:
							<ol>
								<li>Cadastrar Projeto Acad�mico (esta opera��o em quest�o)</li>
								<li>Encaminhar projeto para a PROPLAN atrav�s do Portal Docente > Conv�nios > Projeto/Plano de Trabalho > Submeter Proposta.</li>
							</ol>
						Nestes casos, o projeto ser� cadastrado como REGISTRADO e s� entrar� em execu��o ap�s a finaliza��o dos instrumentos legais.	
					</p>
				</td>
				
			</tr>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>