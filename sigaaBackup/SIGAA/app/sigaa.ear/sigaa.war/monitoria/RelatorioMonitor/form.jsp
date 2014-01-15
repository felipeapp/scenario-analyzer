<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/discente/menu_discente.jsp"%>
	<h2><ufrn:subSistema /> > Relatório do Monitor</h2>

		
		<div class="descricaoOperacao"> 
					Caro Discente, nesta operação você pode enviar os relatórios do projeto de ensino que você participa. Preencha	os dados abaixo e e clique em cadastrar.
		</div>

		<h:form id="relatorio">

			<h:messages/>
				
			<h:inputHidden value="#{relatorioMonitor.confirmButton}" />
			<h:inputHidden value="#{relatorioMonitor.obj.id}"/>
			<h:inputHidden value="#{relatorioMonitor.obj.discenteMonitoria.id}" />

				
			<table class="formulario" width="99%">
				<caption class="listagem"><h:outputText value="#{relatorioMonitor.obj.tipoRelatorio.descricao}"/> DE MONITORIA</caption>


				<tr>
					<td><b>Projeto de Ensino: </b><br/> 
					<h:outputText value="#{relatorioMonitor.obj.discenteMonitoria.projetoEnsino.titulo}"/>
					<br/>
					<br/>					
					</td>
				</tr>
	
			<tr>
				<td><b> 1- Você teve a oportunidade de ler e conhecer o Projeto de Ensino ao qual está vinculado? </b> <br />
				<h:selectOneRadio
					value="#{relatorioMonitor.obj.oportunidadeConhecerProjeto}"
					id="radioOportunidadeConhecerProjeto" rendered="#{!relatorioMonitor.readOnly}">
					<f:selectItem itemLabel="Sim" itemValue="1" />
					<f:selectItem itemLabel="Não" itemValue="2" />
					<f:selectItem itemLabel="Em Parte" itemValue="3" />
				</h:selectOneRadio>
				<h:outputText
					value="#{relatorioMonitor.obj.oportunidadeConhecerProjeto == 1 ? 'Sim': (relatorioMonitor.obj.oportunidadeConhecerProjeto == 2 ? 'Não' : (relatorioMonitor.obj.oportunidadeConhecerProjeto == 3 ? 'Em parte' : '---'))}"
					id="radioOportunidadeConhecerProjeto2"  rendered="#{relatorioMonitor.readOnly}"/>				
					
				</td>
			</tr>


			<tr>
				<td><b> 2- Enumere as atividades desenvolvidas por você no projeto: </b></td>
			</tr>
			<tr>
				<td>
					<h:inputTextarea id="txaAtividadesDesenvolvidas"
						value="#{relatorioMonitor.obj.atividadesDesenvolvidas}" rows="5"
						style="width:98%" readonly="#{relatorioMonitor.readOnly}" />
					
				</td>
			</tr>

			<tr>
				<td><b> 3- Essas atividades desenvolvidas estão coerentes com os objetivos propostos no projeto? </b> <br />

				<h:selectOneRadio
					value="#{relatorioMonitor.obj.coerenciaAtividades}"
					id="radioCoerenciaAtividades" rendered="#{!relatorioMonitor.readOnly}">
					<f:selectItem itemLabel="Sim" itemValue="1" />
					<f:selectItem itemLabel="Não" itemValue="2" />
					<f:selectItem itemLabel="Em Parte" itemValue="3" />
				</h:selectOneRadio>
				
				<h:outputText
					value="#{relatorioMonitor.obj.coerenciaAtividades == 1 ? 'Sim': (relatorioMonitor.obj.coerenciaAtividades == 2 ? 'Não' : (relatorioMonitor.obj.coerenciaAtividades == 3 ? 'Em parte' : '---'))}"
					id="radioCoerenciaAtividades2" rendered="#{relatorioMonitor.readOnly}"/>
				
			</tr>


			<tr>
				<td>3.1- Justifique sua resposta: <br />


				<h:inputTextarea id="txaCoerenciaAtividadesJustificativa"
					value="#{relatorioMonitor.obj.coerenciaAtividadesJustificativa}"
					rows="5" style="width:98%" readonly="#{relatorioMonitor.readOnly}" />
				</td>
			</tr>




			<tr>
				<td><b>4- Como você avalia as orientações recebidas para o desenvolvimento das atividades? 
				 Justifique sua resposta.</b> <br />


				<h:inputTextarea id="txaAvaliacaoOrientacoes"
					value="#{relatorioMonitor.obj.avaliacaoOrientacoes}"
					rows="5" style="width:98%" readonly="#{relatorioMonitor.readOnly}" />
				</td>
			</tr>


			<tr>
				<td><b>5- Que avaliação você faz de sua participação no SID?</b> <br />


				<h:selectOneRadio
					value="#{relatorioMonitor.obj.avaliacaoParticipacao}"
					id="radioAvaliacaoParticipacao" rendered="#{!relatorioMonitor.readOnly}">
					<f:selectItem itemLabel="Satisfatória" itemValue="1" />
					<f:selectItem itemLabel="Regular" itemValue="2" />
					<f:selectItem itemLabel="Ruim" itemValue="3" />
				</h:selectOneRadio>
				
				<h:outputText
					value="#{relatorioMonitor.obj.avaliacaoParticipacao == 1 ? 'Satisfatória': (relatorioMonitor.obj.avaliacaoParticipacao == 2 ? 'Regular' : (relatorioMonitor.obj.avaliacaoParticipacao == 3 ? 'Ruim' : '---'))}"
					id="avaliacaoParticipacao2" rendered="#{relatorioMonitor.readOnly}" />
				
				
				</td>
			</tr>
			
			<tr>
				<td>5.1-Justifique sua resposta.<br />


				<h:inputTextarea id="txaAvaliacaoParticipacaoJustificativa"
					value="#{relatorioMonitor.obj.avaliacaoParticipacaoJustificativa}"
					rows="5" style="width:98%" readonly="#{relatorioMonitor.readOnly}" />
				</td>
			</tr>	
			
			<tr>
				<td><b>6- O programa de monitoria tem contribuído para a sua formação acadêmica? Comente.</b> <br />


				<h:inputTextarea id="txaContribuicaoPrograma"
					value="#{relatorioMonitor.obj.contribuicaoPrograma}"
					rows="5" style="width:98%" readonly="#{relatorioMonitor.readOnly}" />
				</td>
			</tr>					
	
			<tr>
				<td><b>7- Com base no seu desempenho no projeto de monitoria, apresente:</b> <br/>
				7.1- Pontos fortes: 

				<h:inputTextarea id="txaPontosFortesDesempenho"
					value="#{relatorioMonitor.obj.pontosFortesDesempenho}"
					rows="5" style="width:98%" readonly="#{relatorioMonitor.readOnly}" />
				</td>
			</tr>	
			
			<tr>
				<td>7.2- Pontos fracos: <br/>

				<h:inputTextarea id="txaPontosFracosDesempenho"
					value="#{relatorioMonitor.obj.pontosFracosDesempenho}"
					rows="5" style="width:98%" readonly="#{relatorioMonitor.readOnly}" />
				</td>
			</tr>			

			<tfoot>
				<tr>
					<td>
						<input type="hidden" name="id" value="${relatorioMonitor.obj.id}">
						<h:commandButton id="btSalvar" value="Salvar (Rascunho)" action="#{relatorioMonitor.cadastrar}" rendered="#{!relatorioMonitor.readOnly}"/> 
						<h:commandButton id="btConfirmar" value="Enviar Relatório" action="#{relatorioMonitor.enviar}" rendered="#{!relatorioMonitor.readOnly}"/>
						<h:commandButton id="btRemover" value="Remover" action="#{relatorioMonitor.inativar}" rendered="#{relatorioMonitor.readOnly}" />
						<h:commandButton value="Cancelar" action="#{relatorioMonitor.cancelar}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
			
			</table>
		</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>