<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
<!--

/* Paineis de opçoes */
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
	
	<h2><ufrn:subSistema /> &gt; Submissão de proposta de ação acadêmica</h2>

	<h:form id="form" >
		<table id="painel_opcoes" class="formulario">
			<caption>Selecione o tipo de proposta</caption>
			
			<tr>			
				<td align="justify">
					<img src="${ctx}/img/projetos/document_new.png"/>
					<h:commandLink action="#{projetoBase.iniciarProjetoInternoComFinanciamentoInterno}"
						value="Projeto Com Financiamento Interno (Edital)" id="lnkIniciarProjetoInternoComFinanciamentoInterno"/>
					<p>
						Escolha esta opção caso deseje cadastrar um projeto acadêmico com apoio financeiro	
						de algum Edital Interno a ${ configSistema['siglaInstituicao'] }. Nestes casos, o projeto deverá seguir os seguintes passos:
							<ol>
								<li>Cadastrar Projeto Acadêmico selecionando um edital específico (esta operação em questão).</li>
								<li>Avaliação do projeto conforme normas do edital selecionado.</li>
							</ol>
						O projeto será submetido às regras determinadas no edital e deverá entrar em execução conforme publicação do seu resultado.
					</p>
				</td>			
			
				<td align="justify">
					<img src="${ctx}/img/projetos/document.png"/>
					<h:commandLink action="#{projetoBase.iniciarProjetoInternoSemFinanciamentoInterno}"	
						value="Projeto Sem Financiamento Interno" id="lnkIniciarProjetoInternoSemFinanciamentoInterno"/>
					<p>
						Escolha esta opção caso deseje cadastrar um projeto acadêmico que NÃO necessita de apoio financeiro da ${ configSistema['siglaInstituicao'] }. 
						Nestes casos, o projeto deverá seguir os seguintes passos:
							<ol>
								<li>Cadastrar Projeto Acadêmico (esta operação em questão).</li>
								<li>Avaliação do projeto pelo Comitê de Ensino, Pesquisa e Extensão.</li>
							</ol>
						O projeto deverá entrar em execução conforme o resultado das avaliações realizadas pelo Comitê.
					</p>
				</td>			
			</tr>
			
			<tr>						
				<td align="justify">
					<img src="${ctx}/img/projetos/document_ok.png"/>
					<h:commandLink action="#{projetoBase.iniciarProjetoExternoPesquisadorIndividual}"	
						value=" Projeto já Aprovado em Editais de Pesquisador Individual" 
						id="lnkIniciarProjetoExternoPesquisadorIndividual"/>
					<p>
						Escolha esta opção caso deseje cadastrar projetos que foram aprovados junto ao CNPQ, FAPERN, etc. A 
						relação é entre o órgão financiador e o pesquisador. Não há recebimento de recursos nem execução na ${ configSistema['siglaInstituicao'] }.<br/>
						Nestes casos, o seu projeto já será considerado EM EXECUÇÂO.
					</p>
				</td>
				
				<td align="justify">
					<img src="${ctx}/img/projetos/document_into.png"/>
					<h:commandLink action="#{projetoBase.iniciarProjetoExternoConvenio}"	
						value="Projeto para Tramitação de Instrumento Jurídico na #{ configSistema['siglaInstituicao'] }" 
						id="lnkIniciarProjetoExternoConvenio"/>
					<p>
						Escolha esta opção caso deseje cadastrar um projeto acadêmico que será base para um convênio, 
						contrato, termo de cooperação ou outro instrumento.  Nestes casos, o projeto deverá seguir os 
						seguintes passos:
							<ol>
								<li>Cadastrar Projeto Acadêmico (esta operação em questão)</li>
								<li>Encaminhar projeto para a PROPLAN através do Portal Docente > Convênios > Projeto/Plano de Trabalho > Submeter Proposta.</li>
							</ol>
						Nestes casos, o projeto será cadastrado como REGISTRADO e só entrará em execução após a finalização dos instrumentos legais.	
					</p>
				</td>
				
			</tr>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>