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
	<h2><ufrn:subSistema/> &gt; Minhas Solicitações de Orientação de Normalização </h2>

	<div class="descricaoOperacao">
		<p>Caro usuário(a),</p>
		<p>Abaixo podem ser visualizadas as suas <strong> solicitações de orientação de normalização</strong>. 
		</p>
		<p>A normalização é uma validação de um trabalho acadêmico, essa validação verifica se ele encontra-se dentro das normas estruturais estabelecidas. 
		Isso inclui a estrutura do documento, referências bibliográficas, formatação, entre outras.
		</p>   
		<p>
		    <strong> Uma solicitação de orientação é uma reunião agendada entre usuário e o bibliotecário, na qual ele não realizará, mas orientará o usuário em como fazer a normalização de seu trabalho
		    de acordo com as normas existentes.</strong>.  
		</p>
		<br/><br/>
		<p> Uma solicitação de orientação pode estar em uma das situações mostradas abaixo:</p>
		<ul>
			<li>Solicitada: Indica que o usuário solicitou o agendamento de orientação de normalização, mas ainda não foi atendido por um bibliotecário.</li>
			<li>Atendida: Indica que o bibliotecário atendeu a solicitação, agendado um horário para a orientação da normalização, mas o senhor(a) ainda não confirmou o comparecimento.</li>
			<li>Confirmado: Indica que o senhor(a) aprovou o horário definido pelo bibliotecário e confirmou o comparecimento no horário agendado.</li>
			<li>Não Confirmado: Indica que o senhor não pôde comparecer no dia e horário agendado.</li>
			<li>Cancelado: Indica que o bibliotecário não vai atendar a sua solicitação por algum motivo especificado.</li>
		</ul>
	</div>

	<h:form>
		
		<a4j:keepAlive beanName="solicitacaoOrientacaoMBean"></a4j:keepAlive>
		
		<div class="infoAltRem">
			<h:graphicImage value="/img/clock.png" style="overflow: visible;"
				rendered="#{solicitacaoOrientacaoMBean.quantidadeBibliotecasRealizandoOrientacaoNormalizacao > 0}"/> 
			<h:commandLink value=": Agendar Orientação" action="#{solicitacaoOrientacaoMBean.realizarNovaSolicitacao}" 
				rendered="#{solicitacaoOrientacaoMBean.quantidadeBibliotecasRealizandoOrientacaoNormalizacao > 0}"/>
	        
	        <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar
	        <h:graphicImage value="/img/check.png" style="overflow: visible;"/>: Aprovar/Confirmar
	        <h:graphicImage value="/img/delete_old.gif" style="overflow: visible;"/>: Não Aprovar/Cancelar 
	        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
		</div>
		
		<table class=listagem>
			<caption class="listagem">Minhas Solicitações de Orientação de Normalização</caption>
			<c:if test="${not empty solicitacaoOrientacaoMBean.solicitacoes}">
				<thead>
					<tr>
						<%-- <th>Tipo de serviço</th> --%>
						<th>Número</th>
						<th>Biblioteca onde a solicitação se encontra</th>
						<th>Horário agendado</th>
						<th style="text-align:center;width:100px;">Data da Solicitação</th>
						<th style="text-align:center;width:100px;">Situação</th>
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
				<tr><td style="text-align:center;font-weight:bold;color:#FF0000;">Não há solicitações cadastradas.</td></tr>
			</c:if>
			
		</table>
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>