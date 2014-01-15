<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	<h2><ufrn:subSistema /> > Relatório do Monitor</h2>

	<h:form id="form">
			<table class="formulario" width="95%">
				<caption class="listagem"><h:outputText value="#{relatorioMonitor.obj.tipoRelatorio.descricao}"/> DE MONITORIA</caption>

			<tr>
				<td colspan="2"><b>Projeto de ensino: </b><br/> 
				<h:outputText value="#{relatorioMonitor.obj.discenteMonitoria.projetoEnsino.titulo}"/>
				</td>
			</tr>


			<tr>
				<td colspan="2"><b>Discente: </b><br/> 
				<h:outputText value="#{relatorioMonitor.obj.discenteMonitoria.discente.matriculaNome}"/>
				</td>
			</tr>

			<tr>
				<td colspan="2"><b>Tipo de vínculo: </b><br/> 
				<h:outputText value="#{relatorioMonitor.obj.discenteMonitoria.tipoVinculo.descricao}"/>
				</td>
			</tr>

			<tr>
				<td colspan="2"><b>Situação do relatório: </b><br/> 
				<h:outputText value="#{relatorioMonitor.obj.status.descricao}"/>
				</td>
			</tr>
			
			<tr>
				<td><b> Data de envio: </b><br/>
					<i>
						<fmt:formatDate value="${relatorioMonitor.obj.dataEnvio}" pattern="dd/MM/yyyy HH:mm:ss"/>
						<h:outputText id="txaAviso" value="RELATÓRIO AINDA NÃO ENVIADO PARA PROGRAD!" rendered="#{empty relatorioMonitor.obj.dataEnvio}" />
					</i>					
				</td>
			</tr>
			
			<tr>
				<td><hr/></td>
			</tr>
	
			<tr>
				<td align="justify"><b> 1- Você teve a oportunidade de ler e conhecer o Projeto de Ensino ao qual está vinculado? </b> <br />
				<h:outputText
					value="#{relatorioMonitor.obj.oportunidadeConhecerProjeto == 1 ? 'Sim': (relatorioMonitor.obj.oportunidadeConhecerProjeto == 2 ? 'Não' : 'Em parte')}"
					id="radioOportunidadeConhecerProjeto" />
				</td>
			</tr>


			<tr>
				<td align="justify"><b> 2- Enumere as atividades desenvolvidas por você no projeto: </b> <br />

				<h:outputText id="txaAtividadesDesenvolvidas"
					value="#{relatorioMonitor.obj.atividadesDesenvolvidas}"/>
					
				</td>
			</tr>

			<tr>
				<td align="justify"><b> 3- Essas atividades desenvolvidas estão coerentes com os objetivos propostos no projeto? </b> <br />
				<h:outputText
					value="#{relatorioMonitor.obj.coerenciaAtividades == 1 ? 'Sim': (relatorioMonitor.obj.coerenciaAtividades == 2 ? 'Não' : 'Em parte')}"
					id="radioCoerenciaAtividades" />
			</tr>


			<tr>
				<td align="justify"><b><i>3.1- Justifique sua resposta:</i></b><br />


				<h:outputText id="txaCoerenciaAtividadesJustificativa"
					value="#{relatorioMonitor.obj.coerenciaAtividadesJustificativa}"/>
				</td>
			</tr>




			<tr>
				<td align="justify"><b>4- Como você avalia as orientações recebidas para o desenvolvimento das atividades? 
				 Justifique sua resposta.</b> <br />


				<h:outputText id="txaAvaliacaoOrientacoes"
					value="#{relatorioMonitor.obj.avaliacaoOrientacoes}"/>
				</td>
			</tr>


			<tr>
				<td align="justify"><b>5- Que avaliação você faz de sua participação no SID?</b> <br />
				<h:outputText
					value="#{relatorioMonitor.obj.avaliacaoParticipacao == 1 ? 'Satisfatória': (relatorioMonitor.obj.avaliacaoParticipacao == 2 ? 'Regular' : 'Ruim')}"
					id="avaliacaoParticipacao" />
				</td>
			</tr>
			
			<tr>
				<td align="justify"><b><i>5.1-Justifique sua resposta.</i></b><br />


				<h:outputText id="txaAvaliacaoParticipacaoJustificativa"
					value="#{relatorioMonitor.obj.avaliacaoParticipacaoJustificativa}"/>
				</td>
			</tr>	
			
			<tr>
				<td align="justify"><b>6- O programa de monitoria tem contribuído para a sua formação acadêmica? Comente.</b> <br />


				<h:outputText id="txaContribuicaoPrograma"
					value="#{relatorioMonitor.obj.contribuicaoPrograma}" />
				</td>
			</tr>					
	
			<tr>
				<td align="justify"><b>7- Com base no seu desempenho no projeto de monitoria, apresente:</b> <br/>
					<b><i>7.1- Pontos fortes: </i></b><br/>

				<h:outputText id="txaPontosFortesDesempenho"
					value="#{relatorioMonitor.obj.pontosFortesDesempenho}" />
				</td>
			</tr>	
			
			<tr>
				<td align="justify"><b><i>7.2- Pontos fracos:</i></b> <br/>

				<h:outputText id="txaPontosFracosDesempenho"
					value="#{relatorioMonitor.obj.pontosFracosDesempenho}" />
				</td>
			</tr>			

			<tr>
				<td colspan="2" class="subFormulario"> Validação do relatório de desligamento</td>
			</tr>			


			<tr>
				<td align="justify"><b>Parecer da Coordenação:</b>
				<h:selectOneRadio
					value="#{relatorioMonitor.obj.coordenacaoValidouDesligamento}"	id="cmbCoordenacaoValidouDesligamento">
					<f:selectItem itemLabel="Validar" itemValue="true" />
					<f:selectItem itemLabel="Não Validar" itemValue="false" />
				</h:selectOneRadio>				
				</td>
			</tr>

			<tr>
				<td>Observações: <br/>
				<h:inputTextarea id="txtObservacoes"
					value="#{relatorioMonitor.obj.observacaoCoordenacaoDesligamento}"
					rows="5" style="width:98%" readonly="#{relatorioMonitor.readOnly}" />
				</td>
			</tr>			

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btConfirmar" value="Confirmar Parecer" action="#{relatorioMonitor.coordenacaoValidarRelatorioDesligamento}" />
						<h:commandButton value="Cancelar" action="#{relatorioMonitor.cancelar}" onclick="#{confirm}"/>
						<input type="button" value="Imprimir" onclick="javascript:window.print()"/>		
					</td>
				</tr>
			</tfoot>

		</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>