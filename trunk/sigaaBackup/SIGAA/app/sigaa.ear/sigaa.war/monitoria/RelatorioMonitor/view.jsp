<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.TipoRelatorioMonitoria"%>
<f:view>
	<h2>Relat�rio do Monitor</h2>


			<c:set var="RELATORIO_DESLIGAMENTO"		value="<%= String.valueOf(TipoRelatorioMonitoria.RELATORIO_DESLIGAMENTO_MONITOR) %>" 		scope="application"/>

			<table class="tabelaRelatorio" width="95%">
				<caption class="listagem"><h:outputText value="#{relatorioMonitor.obj.tipoRelatorio.descricao}"/> DE MONITORIA</caption>

			<tr>
				<td colspan="2"><b>Projeto de ensino: </b><br/> 
				<h:outputText value="#{relatorioMonitor.obj.discenteMonitoria.projetoEnsino.anoTitulo}"/>
				</td>
			</tr>

			<tr>
				<td colspan="2"><b>Discente: </b><br/> 
				<h:outputText value="#{relatorioMonitor.obj.discenteMonitoria.discente.matriculaNome}"/>
				</td>
			</tr>

			<tr>
				<td colspan="2"><b>Curso: </b><br/> 
				<h:outputText value="#{relatorioMonitor.obj.discenteMonitoria.discente.curso.descricao}"/>
				</td>
			</tr>

			<tr>
				<td colspan="2"><b>V�nculo: </b><br/> 
				<h:outputText value="#{relatorioMonitor.obj.discenteMonitoria.tipoVinculo.descricao}"/>
				</td>
			</tr>

			<tr>
				<td colspan="2"><b> Data de envio: </b><br/>
					<i>
						<fmt:formatDate value="${relatorioMonitor.obj.dataEnvio}" pattern="dd/MM/yyyy HH:mm:ss"/>
						<h:outputText id="txaAviso" value="RELAT�RIO AINDA N�O ENVIADO PARA PROGRAD!" rendered="#{empty relatorioMonitor.obj.dataEnvio}" />
					</i>					
				</td>
			</tr>
			
			<tr>
				<td align="justify" colspan="2"><b> 1- Voc� teve a oportunidade de ler e conhecer o Projeto de Ensino ao qual est� vinculado? </b> <br />
				<h:outputText
					value="#{relatorioMonitor.obj.oportunidadeConhecerProjeto == 1 ? 'Sim': (relatorioMonitor.obj.oportunidadeConhecerProjeto == 2 ? 'N�o' : (relatorioMonitor.obj.oportunidadeConhecerProjeto == 3 ? 'Em parte' : '---'))}"
					id="radioOportunidadeConhecerProjeto" />
				</td>
			</tr>


			<tr>
				<td align="justify" colspan="2"><b> 2- Enumere as atividades desenvolvidas por voc� no projeto: </b> <br />

				<h:outputText id="txaAtividadesDesenvolvidas"
					value="#{relatorioMonitor.obj.atividadesDesenvolvidas}"/>
					
				</td>
			</tr>

			<tr>
				<td align="justify" colspan="2"><b> 3- Essas atividades desenvolvidas est�o coerentes com os objetivos propostos no projeto? </b> <br />
				<h:outputText
					value="#{relatorioMonitor.obj.coerenciaAtividades == 1 ? 'Sim': (relatorioMonitor.obj.coerenciaAtividades == 2 ? 'N�o' : (relatorioMonitor.obj.coerenciaAtividades == 3 ? 'Em parte' : '---'))}"
					id="radioCoerenciaAtividades" />
			</tr>


			<tr>
				<td align="justify" colspan="2"><b><i>3.1- Justifique sua resposta:</i></b><br />


				<h:outputText id="txaCoerenciaAtividadesJustificativa"
					value="#{relatorioMonitor.obj.coerenciaAtividadesJustificativa}"/>
				</td>
			</tr>




			<tr>
				<td align="justify" colspan="2"><b>4- Como voc� avalia as orienta��es recebidas para o desenvolvimento das atividades? 
				 Justifique sua resposta.</b> <br />

				<h:outputText id="txaAvaliacaoOrientacoes"
					value="#{relatorioMonitor.obj.avaliacaoOrientacoes}"/>
				</td>
			</tr>


			<tr>
				<td align="justify" colspan="2"><b>5- Que avalia��o voc� faz de sua participa��o no SID?</b> <br />
				<h:outputText
					value="#{relatorioMonitor.obj.avaliacaoParticipacao == 1 ? 'Satisfat�ria': (relatorioMonitor.obj.avaliacaoParticipacao == 2 ? 'Regular' : (relatorioMonitor.obj.avaliacaoParticipacao == 3 ? 'Ruim' : '---'))}"
					id="avaliacaoParticipacao" />
				</td>
			</tr>
			
			<tr>
				<td align="justify" colspan="2"><b><i>5.1-Justifique sua resposta.</i></b><br />


				<h:outputText id="txaAvaliacaoParticipacaoJustificativa"
					value="#{relatorioMonitor.obj.avaliacaoParticipacaoJustificativa}"/>
				</td>
			</tr>	
			
			<tr>
				<td align="justify" colspan="2"><b>6- O programa de monitoria tem contribu�do para a sua forma��o acad�mica? Comente.</b> <br />


				<h:outputText id="txaContribuicaoPrograma"
					value="#{relatorioMonitor.obj.contribuicaoPrograma}" />
				</td>
			</tr>					
	
			<tr>
				<td align="justify" colspan="2"><b>7- Com base no seu desempenho no projeto de monitoria, apresente:</b> <br/>
					<b><i>7.1- Pontos fortes: </i></b><br/>

				<h:outputText id="txaPontosFortesDesempenho"
					value="#{relatorioMonitor.obj.pontosFortesDesempenho}" />
				</td>
			</tr>	
			
			<tr>
				<td align="justify" colspan="2"><b><i>7.2- Pontos fracos:</i></b> <br/>

				<h:outputText id="txaPontosFracosDesempenho"
					value="#{relatorioMonitor.obj.pontosFracosDesempenho}" />
				</td>
			</tr>			

		<c:if test="${relatorioMonitor.obj.tipoRelatorio.id == RELATORIO_DESLIGAMENTO}">
			<tr>
				<td> </td>
			</tr>
			
			<tr>
			
				<td class="subFormulario" style="background-color: white; color: black; border-top: 1px solid black; border-bottom: 1px solid #000000" colspan="2" >Situa��o do Relat�rio</td>
			</tr>			

			
			<tr>
				<td><b>Situa��o do relat�rio:</b><br/>
					<h:outputText value="#{relatorioMonitor.obj.status.descricao}"/>
				</td>
			</tr>


			<tr>
				<td ><b>An�lise da Coordena��o do Projeto:</b><br/>
						<h:outputText id="txtCoordenadorValidou"
							value="#{relatorioMonitor.obj.coordenacaoValidouDesligamento ? 'VALIDADO em ' : (relatorioMonitor.obj.coordenacaoValidouDesligamento == null ? 'PENDENTE' : 'N�O VALIDADO em ')}" />
						<h:outputText id="txtCoordenadorValidouData"
							value="#{relatorioMonitor.obj.coordenacaoValidouDesligamento ? relatorioMonitor.obj.registroValidacaoCoordenacaoDesligamento.data : (relatorioMonitor.obj.coordenacaoValidouDesligamento == null ? '' : relatorioMonitor.obj.registroValidacaoCoordenacaoDesligamento.data)}" />					

						<h:outputText id="txtNomeCoordenador"
							value=" por #{relatorioMonitor.obj.registroValidacaoCoordenacaoDesligamento.usuario.nome}" rendered="#{relatorioMonitor.obj.coordenacaoValidouDesligamento}" escape="false"/>
					<i>
						<br/><h:outputText value="#{relatorioMonitor.obj.observacaoCoordenacaoDesligamento}" />
					</i>
				</td>
			</tr>
			
			<tr>
				<td><b>An�lise da Pr�-Reitoria de Gradua��o:</b><br/>
						<h:outputText id="txtProgradValidou"
							value="#{relatorioMonitor.obj.progradValidouDesligamento ? 'VALIDADO em ' : (relatorioMonitor.obj.progradValidouDesligamento == null ? 'PENDENTE' : 'N�O VALIDADO em ')}" />
						<h:outputText id="txtProgradValidouData"
							value="#{relatorioMonitor.obj.progradValidouDesligamento ? relatorioMonitor.obj.registroValidacaoProgradDesligamento.data : (relatorioMonitor.obj.progradValidouDesligamento == null ? '' : relatorioMonitor.obj.registroValidacaoProgradDesligamento.data)}" />					
					<i>
						<br/><h:outputText value="#{relatorioMonitor.obj.observacaoProgradDesligamento}" />
					</i>
				
				</td>
			</tr>			
		</c:if>		
	</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>