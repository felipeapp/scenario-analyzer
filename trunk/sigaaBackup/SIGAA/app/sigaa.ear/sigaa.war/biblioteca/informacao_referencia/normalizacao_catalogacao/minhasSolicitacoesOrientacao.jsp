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
	<h2><ufrn:subSistema/> &gt; Minhas Solicita��es de Orienta��o de Normaliza��o </h2>

	<div class="descricaoOperacao">
		<p>Caro usu�rio(a),</p>
		<p>Abaixo podem ser visualizadas as suas <strong> solicita��es de orienta��o de normaliza��o</strong>. 
		</p>
		<p>A normaliza��o � uma valida��o de um trabalho acad�mico, essa valida��o verifica se ele encontra-se dentro das normas estruturais estabelecidas. 
		Isso inclui a estrutura do documento, refer�ncias bibliogr�ficas, formata��o, entre outras.
		</p>   
		<p>
		    <strong> Uma solicita��o de orienta��o � uma reuni�o agendada entre usu�rio e o bibliotec�rio, na qual ele n�o realizar�, mas orientar� o usu�rio em como fazer a normaliza��o de seu trabalho
		    de acordo com as normas existentes.</strong>.  
		</p>
		<br/><br/>
		<p> Uma solicita��o de orienta��o pode estar em uma das situa��es mostradas abaixo:</p>
		<ul>
			<li>Solicitada: Indica que o usu�rio solicitou o agendamento de orienta��o de normaliza��o, mas ainda n�o foi atendido por um bibliotec�rio.</li>
			<li>Atendida: Indica que o bibliotec�rio atendeu a solicita��o, agendado um hor�rio para a orienta��o da normaliza��o, mas o senhor(a) ainda n�o confirmou o comparecimento.</li>
			<li>Confirmado: Indica que o senhor(a) aprovou o hor�rio definido pelo bibliotec�rio e confirmou o comparecimento no hor�rio agendado.</li>
			<li>N�o Confirmado: Indica que o senhor n�o p�de comparecer no dia e hor�rio agendado.</li>
			<li>Cancelado: Indica que o bibliotec�rio n�o vai atendar a sua solicita��o por algum motivo especificado.</li>
		</ul>
	</div>

	<h:form>
		
		<a4j:keepAlive beanName="solicitacaoOrientacaoMBean"></a4j:keepAlive>
		
		<div class="infoAltRem">
			<h:graphicImage value="/img/clock.png" style="overflow: visible;"
				rendered="#{solicitacaoOrientacaoMBean.quantidadeBibliotecasRealizandoOrientacaoNormalizacao > 0}"/> 
			<h:commandLink value=": Agendar Orienta��o" action="#{solicitacaoOrientacaoMBean.realizarNovaSolicitacao}" 
				rendered="#{solicitacaoOrientacaoMBean.quantidadeBibliotecasRealizandoOrientacaoNormalizacao > 0}"/>
	        
	        <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar
	        <h:graphicImage value="/img/check.png" style="overflow: visible;"/>: Aprovar/Confirmar
	        <h:graphicImage value="/img/delete_old.gif" style="overflow: visible;"/>: N�o Aprovar/Cancelar 
	        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
		</div>
		
		<table class=listagem>
			<caption class="listagem">Minhas Solicita��es de Orienta��o de Normaliza��o</caption>
			<c:if test="${not empty solicitacaoOrientacaoMBean.solicitacoes}">
				<thead>
					<tr>
						<%-- <th>Tipo de servi�o</th> --%>
						<th>N�mero</th>
						<th>Biblioteca onde a solicita��o se encontra</th>
						<th>Hor�rio agendado</th>
						<th style="text-align:center;width:100px;">Data da Solicita��o</th>
						<th style="text-align:center;width:100px;">Situa��o</th>
						<th width="20"></th>
						<th width="20"></th>
						<th width="20"></th>
						<th width="20"></th>
						<th width="20"></th>
					</tr>
				</thead>
				<c:forEach items="#{solicitacaoOrientacaoMBean.solicitacoes}" var="solicitacao" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						
						<td>${solicitacao.numeroSolicitacao}</td>
						<td>${solicitacao.biblioteca.descricao}</td>
						<td>${solicitacao.atendido || solicitacao.confirmado ? solicitacao.descricaoHorarioAtendimento : 'N/A'}</td>
						<td style="text-align:center;"><ufrn:format type="data" valor="${solicitacao.dataCadastro}"/></td>
						<td style="text-align:center;">${solicitacao.situacao.descricao}</td>
						
						<td width="20">
							<h:commandLink
									action="#{solicitacaoOrientacaoMBean.visualizarDadosSolicitacao}"
									title="Visualizar" >
								<f:param name="idSolicitacao" value="#{solicitacao.id}" />
								<h:graphicImage url="/img/view.gif" alt="Visualizar" />
							</h:commandLink>
						</td>
						
						<td width="20">
							<c:if test="${solicitacao.solicitado}">
								<h:commandLink
										action="#{solicitacaoOrientacaoMBean.alterarSolicitacao}"
										title="Alterar">
									<f:param name="idSolicitacao" value="#{solicitacao.id}" />
									<h:graphicImage url="/img/alterar.gif" alt="Alterar" />
								</h:commandLink>
							</c:if>
						</td>
						
						<td width="20">
							<c:if test="${solicitacao.atendido}">
								<h:commandLink action="#{solicitacaoOrientacaoMBean.confirmarSolicitacao}">
									<h:graphicImage id="gphicimgConfirmarSolicitacao" url="/img/check.png" style="border:none"
										title="Confirmar" />
									<f:param name="idSolicitacao" value="#{solicitacao.id}"/>		
								</h:commandLink>
							</c:if>
						</td>
						
						<td width="20">
							<c:if test="${solicitacao.atendido}">
								<h:commandLink action="#{solicitacaoOrientacaoMBean.cancelarSolicitacao}">
									<h:graphicImage id="gphicimgCancelarSolicitacao" url="/img/delete_old.gif" style="border:none"
											title="Cancelar" />
										<f:param name="idSolicitacao" value="#{solicitacao.id}"/>		
								</h:commandLink>
							</c:if>
						</td>
						
						<td width="20">
							<c:if test="${solicitacao.solicitado}">
								<h:commandLink
										action="#{solicitacaoOrientacaoMBean.removerSolicitacao}"
										title="Remover" onclick="#{confirmDelete}">
									<f:param name="idSolicitacao" value="#{solicitacao.id}" />
									<h:graphicImage url="/img/delete.gif" alt="Remover" />
								</h:commandLink>
							</c:if>
						</td>
						
					</tr>
				</c:forEach>
			</c:if>
			
			<c:if test="${empty solicitacaoOrientacaoMBean.solicitacoes}">
				<tr><td style="text-align:center;font-weight:bold;color:#FF0000;">N�o h� solicita��es cadastradas.</td></tr>
			</c:if>
			
		</table>
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>