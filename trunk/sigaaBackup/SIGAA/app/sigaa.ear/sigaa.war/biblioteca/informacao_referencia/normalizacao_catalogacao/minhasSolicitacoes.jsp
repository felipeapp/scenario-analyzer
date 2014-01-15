<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoDocumentoNormalizacaoCatalogacao"%>

<style type="text/css">
	div.option_panel {
		display: inline;
	}
	div.option_panel div.labelZ {
		display: inline;
	}
	div.option_panel div.panelZ {
		float: left; 
		border: #DEDEDE solid 2px;
		background-color: #F5F5F5;
		position: fixed;
		z-index: 10;
	}
	div.option_panel div.oculto {
		display: none;
	}
	div.option_panel div.visivel {
		display: inline;
	}
	div.option_panel div.panelZ ul {
		margin: 0px;
		padding: 0px;
	}
	div.option_panel div.panelZ ul li {
		margin: 4px;
	}
</style>

<f:view>

	<h2><ufrn:subSistema/> &gt; Minhas Solicita��es de Normaliza��o e Cataloga��o na Fonte </h2>

	<div class="descricaoOperacao">
		<c:if test="${solicitacaoServicoDocumentoMBean.quantidadeBibliotecasRealizandoNormalizacao > 0}">
			<p> A normaliza��o � uma valida��o de um trabalho acad�mico, essa valida��o verifica se ele encontra-se dentro das normas estruturais estabelecidas.<br/>
			Isso inclui a estrutura do documento, refer�ncias bibliogr�ficas, formata��o, entre outras. </p>
		</c:if>
		<br/>
		<br/>
		<c:if test="${solicitacaoServicoDocumentoMBean.quantidadeBibliotecasRealizandoCatalogacaoNaFonte > 0}">
			<p>Cataloga��o na Fonte � a gera��o da ficha catalogr�fica do seu trabalho de acordo com as normas existentes. Essa ficha dever� ser anexada 
			ao trabalho.</p>
			<p><strong>Observa��o: Caso a solicita��o de Cataloga��o na Fonte seja atendida, a ficha catalogr�fica gerada ser�
			anexada � solicita��o e estar� dispon�vel nesta p�gina.</strong>
			</p>
		</c:if>
	</div>
	
	<c:if test="${exibirOpcaoComprovante}">
		<h:form>
			<table class="subFormulario" align="center" width="100%">
				<caption style="text-align: center;">Informa��o Importante</caption>
				<tr>
				<td width="8%" valign="middle" align="center">
					<html:img page="/img/warning.gif"/>
				</td>
				<td valign="middle" style="text-align: justify">
					Por favor imprima o comprovante clicando no �cone ao lado para maior seguran�a dessa opera��o.
				</td>
				<td>
					<table>
						<tr>
							<td align="center">
								<h:commandLink title="Imprimir Comprovante"  target="_blank" action="#{solicitacaoServicoDocumentoMBean.telaComprovante}">
						 			<h:graphicImage url="/img/printer_ok.png" />
									<f:param name="idSolicitacao" value="#{idSolicitacao}" />
						 		</h:commandLink>
						 	</td>
						 </tr>
						 <tr>
						 	<td style="font-size: medium;">
						 		<h:commandLink title="Imprimir Comprovante"  target="_blank" value="COMPROVANTE" action="#{solicitacaoServicoDocumentoMBean.telaComprovante}">
									<f:param name="idSolicitacao" value="#{idSolicitacao}" />
						 		</h:commandLink>
						 	</td>
						 </tr>
					</table>
				</td>
				</tr>
			</table>
		<br/>
		</h:form>
	</c:if>

	<h:form>
		
		<a4j:keepAlive beanName="solicitacaoServicoDocumentoMBean"></a4j:keepAlive>
		
		<div class="infoAltRem">
			
			<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"
					rendered="#{solicitacaoServicoDocumentoMBean.quantidadeBibliotecasRealizandoNormalizacao > 0}" /> 
			<h:commandLink value=": Solicitar Normaliza��o" action="#{solicitacaoNormalizacaoMBean.realizarNovaSolicitacao}" 
						rendered="#{solicitacaoServicoDocumentoMBean.quantidadeBibliotecasRealizandoNormalizacao > 0}"/>
			
			<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"
					rendered="#{solicitacaoServicoDocumentoMBean.quantidadeBibliotecasRealizandoCatalogacaoNaFonte > 0}"/> 
			<h:commandLink value=": Solicitar Cataloga��o na Fonte" action="#{solicitacaoCatalogacaoMBean.realizarNovaSolicitacao}" 
					rendered="#{solicitacaoServicoDocumentoMBean.quantidadeBibliotecasRealizandoCatalogacaoNaFonte > 0}"/>
			
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar
	        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
	        <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar
		</div>
		
		<table class=listagem>
			<caption class="listagem">Minhas Solicita��es de Normaliza��o e Cataloga��o na Fonte</caption>
			<c:if test="${not empty solicitacaoServicoDocumentoMBean.solicitacoes}">
				<thead>
					<tr>
						<th>N�mero</th>
						<th>Tipo de servi�o</th>
						<th>Tipo de obra</th>
						<th>Biblioteca onde a solicita��o se encontra</th>
						<th style="text-align:center;width:100px;">Data da Solicita��o</th>
						<th style="text-align:center;width:100px;">Situa��o</th>
						<th width="20"></th>
						<th width="20"></th>
						<th width="20"></th>
					</tr>
				</thead>
				<c:forEach items="#{solicitacaoServicoDocumentoMBean.solicitacoes}" var="solicitacao" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${solicitacao.numeroSolicitacao}</td>
						<td>${solicitacao.tipoServico}</td>
						
						<td>
							${solicitacao.tipoDocumento.denominacao}
							<c:if test="${solicitacao.tipoDocumento.tipoDocumentoOutro}">
								(  ${solicitacao.outroTipoDocumento}  )
							</c:if>
						</td>
						
						<td>${solicitacao.biblioteca.descricao}</td>
						<td style="text-align:center;"><ufrn:format type="data" valor="${solicitacao.dataCadastro}"/></td>
						<td style="text-align:center;">${solicitacao.situacao.descricao}</td>
						
						<td width="20">
							<h:commandLink
									action="#{solicitacaoServicoDocumentoMBean.visualizarDadosSolicitacao}"
									title="Visualizar" >
								<f:param name="idSolicitacao" value="#{solicitacao.id}" />
								<h:graphicImage url="/img/view.gif" alt="Visualizar" />
							</h:commandLink>
						</td>
						
						<td width="20">
							<c:if test="${solicitacao.solicitado}">
								<h:commandLink
										action="#{solicitacaoServicoDocumentoMBean.alterarSolicitacao}"
										title="Alterar">
									<f:param name="idSolicitacao" value="#{solicitacao.id}" />
									<h:graphicImage url="/img/alterar.gif" alt="Alterar" />
								</h:commandLink>
							</c:if>
						</td>
						
						<td width="20">
							<c:if test="${solicitacao.solicitado}">
								<h:commandLink
										action="#{solicitacaoServicoDocumentoMBean.removerSolicitacao}"
										title="Remover" onclick="#{confirmDelete}">
									<f:param name="idSolicitacao" value="#{solicitacao.id}" />
									<h:graphicImage url="/img/delete.gif" alt="Remover" />
								</h:commandLink>
							</c:if>
						</td>
						
					</tr>
				</c:forEach>
			</c:if>
			
			<c:if test="${empty solicitacaoServicoDocumentoMBean.solicitacoes}">
				<tr><td style="text-align:center;font-weight:bold;color:#FF0000;">N�o h� solicita��es cadastradas.</td></tr>
			</c:if>
			
		</table>
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>