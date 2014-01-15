<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > ${ relatoriosVestibular.nomeRelatorio }</h2>

	<div class="descricaoOperacao">
		<p>
			<b>Caro Usuário,</b>
		</p>
		<p>Este formulário permite que busque por inscrições de
			candidatos, visualizar, imprimir ou alterar os dados, bem como gerar
			uma segunda via da GRU para pagamento, com a opção de alterar a data
			de vencimento da GRU.</p>
	</div>
	<h:form id="form">
		<table align="center" class="formulario" width="75%">
			<caption class="listagem">Dados dos Documentos</caption>
			<tr>
				<th>CPF:</th>
				<td> 
					<h:inputText value="#{ relatoriosVestibular.pessoa.cpf_cnpj }" maxlength="14" size="14" 
						onkeypress="return formataCPF(this, event, null)" id="txtCPF" disabled="#{relatoriosVestibular.readOnly}">
						<f:converter converterId="convertCpf"/>
					</h:inputText> 
				</td>	
			</tr>
			<tr>
				<th>Nome do Candidato: </th>
				<td> <h:inputText value="#{ relatoriosVestibular.pessoa.nome }" size="38" maxlength="45" /> </td>	
			</tr>
			<tr>
				<th width="30%">Processo Seletivo:</th>
				<td width="70%">
					<h:selectOneMenu id="processoSeletivo" immediate="true"
						value="#{relatoriosVestibular.idProcessoSeletivo}">
						<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
						<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
					  <h:commandButton id="consultar" value="Buscar" action="#{relatoriosVestibular.verInscricao}" /> 
					  <h:commandButton value="Cancelar" action="#{relatoriosVestibular.cancelar}" id="cancelar" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
		<br />

	    <c:if test="${ not empty relatoriosVestibular.inscricoes }">
			<center>
				<div class="infoAltRem">
				    <h:graphicImage value="/img/buscar.gif" style="overflow: visible;" title="Visualizar" alt="Visualizar"/>: Visualizar 
				    <h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Atualizar
				    <h:graphicImage value="/img/printer_ok.png" style="overflow: visible; width: 18px;" title="Imprimir" alt="Imprimir"/>: Imprimir
  				    <h:graphicImage value="/img/gru/boleto.jpeg" style="overflow: visible; width: 18px;" title="Imprimir" alt="Reimprimir GRU"/>: Reimprimir GRU
  				    <h:graphicImage value="/img/check.png" style="overflow: visible; width: 18px;" title="GRU Quitada" alt="GRU Quitada"/>: GRU Quitada
				</div>
			</center>
	
			<table class="listagem">
				<caption>Inscrições Encontradas: (${fn:length(relatoriosVestibular.inscricoes) })</caption>
					<thead>
						<tr>
							<th style="text-align: center;"> CPF </th>
							<th style="text-align: left;"> Candidato </th>
							<th style="text-align: left;"> Processo Seletivo (Inscrição) </th>
							<th colspan="5"> </th>
						</tr>
					</thead>
			
					<c:forEach var="inscritos" items="#{ relatoriosVestibular.inscricoes }" varStatus="status">
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td style="text-align: center;"> <ufrn:format type="cpf_cnpj" valor="${ inscritos.pessoa.cpf_cnpj }"/> </td>
							<td style="text-align: left;"> ${ inscritos.pessoa.nome } </td>
							<td style="text-align: left;">${ inscritos.processoSeletivo.nome } (${ inscritos.numeroInscricao }) </td>
							<td width="20">
								<h:commandLink action="#{ relatoriosVestibular.detalhar }" >
									<h:graphicImage value="/img/buscar.gif" style="overflow: visible;" title="Visualizar"/>
									<f:param name="id" value="#{inscritos.id}"/>
								</h:commandLink>
							</td>
							<td width="20">
								<h:commandLink action="#{ dadosPessoaisCandidatoMBean.atualizar }" >
									<h:graphicImage url="/img/alterar.gif" style="overflow: visible; width: 18px;" title="Atualizar"/>
									<f:param name="id" value="#{inscritos.id}"/>
								</h:commandLink>
							</td>
							<td width="20">
								<h:commandLink action="#{ relatoriosVestibular.detalharImpressao }" >
									<h:graphicImage value="/img/printer_ok.png" style="overflow: visible; width: 18px;" title="Imprimir"/>
									<f:param name="id" value="#{inscritos.id}"/>
								</h:commandLink>
							</td>
							<td width="20">
							    <h:commandLink action="#{ relatoriosVestibular.selecionarGRU }" rendered="#{ inscritos.idGRU != null && !inscritos.gruQuitada}">
									<h:graphicImage value="/img/gru/boleto.jpeg" style="overflow: visible; width: 18px;" title="Reimprimir GRU"/>
									<f:param name="id" value="#{inscritos.id}"/>
								</h:commandLink>      
							</td>
							<td width="20">
								<h:graphicImage value="/img/check.png" style="overflow: visible; width: 18px;" title="GRU Quitada"
								  rendered="#{inscritos.gruQuitada}"/>
							</td>
						</tr>
					</c:forEach>
			</table>
		</c:if>
		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>