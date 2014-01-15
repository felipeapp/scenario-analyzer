
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<%@ taglib uri="/tags/struts-html" prefix="html"%>

<style type="text/css">
	strong { font-weight: bold; }
</style>
	
	<h2>Lista de Mini Atividades com Períodos de Inscrição Abertos</h2>
	
	<div class="descricaoOperacao">
		<p>Caro Usuário(a),</p>
		<p>Abaixo estão listadas as mini atividades da atividade selecionada que possuem períodos de inscrição abertos.</p>
		<br />
		<p><strong>Observação: </strong> As inscrições para as mini atividades só estarão habilitadas após realizar inscrição na atividade principal. </p>
		<br />
	</div>	
	
	<h:form id="formListaMiniAtividadesExtensao">    

		<c:choose>
			<c:when test="${inscricaoParticipanteMiniAtividadeMBean.qtdPeriodosInscricaoAbertos == 0}">
				<center style="color:red;"><i>Nenhuma Mini Atividade com perído de inscrição aberto </i></center>
			</c:when>
	
			<c:otherwise>
				
					<div class="infoAltRem">
						<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Ver Detalhes da Mini Atividade
		    			<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Inscrever-se
					</div>
					<table class="listagem" style="width:100%;">
						<caption class="listagem">Inscrições Abertas ( ${inscricaoParticipanteMiniAtividadeMBean.qtdPeriodosInscricaoAbertos} ) </caption>
						<thead>
							<tr>
								<th rowspan="2"  style="width: 50%;">Título</th>
								<th rowspan="2"  width="10%">Tipo</th>
								<th rowspan="2"  style="text-align:center">Inscrições até</th>
								<th colspan="4"  style="text-align:center;">Vagas</th>
								<th rowspan="2"  width="1%" />
								<th rowspan="2"  width="1%" />
							</tr>
							
							<tr>
								
								<th style="text-align:right">Total </th>
								<th style="text-align:right">Aprovadas</th>
								<th style="text-align:right">Pendentes</th>
								<th style="text-align:right">Restantes</th>
							</tr>
							
						</thead>
						<tbody>
							<c:forEach items="#{inscricaoParticipanteMiniAtividadeMBean.periodosInscricoesAbertos}" var="periodoAberto" varStatus="status">
								<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">
									
									<td>
										<span style="font-weight: bold; ${periodoAberto.estouInscrito ? 'color:green;' : ''} ">${periodoAberto.subAtividade.titulo}</span> <br/>
										<font style="font-size: x-small;"> <i>Coordenação: ${periodoAberto.subAtividade.atividade.coordenacao.pessoa.nome}</i> </font>								
									</td>
									
									<td>${periodoAberto.subAtividade.tipoSubAtividadeExtensao.descricao}</td>
									<td style="text-align:center">
										<h:outputText value="#{periodoAberto.periodoFim}">
											<f:convertDateTime pattern="dd/MM/yyyy" />
										</h:outputText>
									</td>
									
									<td style="text-align:right">${periodoAberto.quantidadeVagas}</td>
				                    <td style="text-align:right">${periodoAberto.quantidadeInscritosAprovados}</td>
				                    <td style="text-align:right">${periodoAberto.quantidadeInscritos}</td>
									<td style="text-align:right">${periodoAberto.quantidadeVagasRestantes}</td>
									
									
									<td align="center">
										<h:commandLink title="Ver Detalhes da Mini Atividade" action="#{inscricaoParticipanteMiniAtividadeMBean.visualizarDadosMiniAtividade}">
											<f:param name="idSubAtividadeExtensaoSelecionada" value="#{periodoAberto.subAtividade.id}" />
											<h:graphicImage url="/img/view.gif" />
										</h:commandLink>
									</td>
									
									<td align="center">
										
										<h:commandLink title="Inscrever-se" action="#{inscricaoParticipanteMiniAtividadeMBean.preIncreverParticipante}"
												rendered="#{inscricaoParticipanteMiniAtividadeMBean.inscricaoMiniAtividadeEstaHabilitada}">
											<f:param name="idPeriodoAbertoSelecionado" value="#{periodoAberto.id}" />
											<h:graphicImage url="/img/seta.gif" />
										</h:commandLink>
										
										<h:graphicImage url="/img/seta_cinza.gif" rendered="#{! inscricaoParticipanteMiniAtividadeMBean.inscricaoMiniAtividadeEstaHabilitada}"/>
										
									</td>
									
								</tr>
							</c:forEach>
						</tbody>
						
						<tfoot>
							<tr>
								<td colspan="9" style="text-align: center;">
									<h:commandButton value="Cancelar" action="#{inscricaoParticipanteAtividadeMBean.telaListaPeriodosInscricoesAtividadesAbertos}" immediate="true"/> 
								</td>
							</tr>
						</tfoot>
						
					</table>
				
			</c:otherwise>
			
		</c:choose>
		
	</h:form>