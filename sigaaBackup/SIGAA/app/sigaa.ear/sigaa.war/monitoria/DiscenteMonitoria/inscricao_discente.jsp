<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<%@include file="/portais/discente/menu_discente.jsp" %>
	
	<h2><ufrn:subSistema /> > Inscrição para seleção de Projeto de Ensino</h2>
	<br>
	
	
	<h:form>
	
		<table width="100%" class="formulario">
			<caption class="listagem"> Dados da Prova Seletiva </caption>
			
			<tbody>
					
					<tr><th width="30%"><b>Projeto Ensino:</b></th><td>${discenteMonitoria.provaSelecao.projetoEnsino.titulo}</td></tr>
					<tr><th><b>Título da Prova:</b></th><td>${discenteMonitoria.provaSelecao.titulo}</td></tr>
					<tr><th><b>Inscrições até:</b></th><td><fmt:formatDate pattern="dd/MM/yyyy" value="${discenteMonitoria.provaSelecao.dataLimiteIncricao}"/></td></tr>
					<tr><th><b>Data da Prova:</b></th><td><fmt:formatDate pattern="dd/MM/yyyy" value="${discenteMonitoria.provaSelecao.dataProva}"/></td></tr>
					<tr><th><b>Vagas p/ Bolsistas:</b></th><td>${discenteMonitoria.provaSelecao.vagasRemuneradas}</td></tr>
					<tr><th><b>Vagas p/ Voluntários:</b></th><td>${discenteMonitoria.provaSelecao.vagasNaoRemuneradas}</td></tr>
					<tr><th><b>Situação da Prova:</b></th><td>${discenteMonitoria.provaSelecao.situacaoProva.descricao}</td></tr>
					<tr><th><b>Outras Informações:</b></th><td>${discenteMonitoria.provaSelecao.informacaoSelecao}</td></tr>
					
					<tr>
						<td class="subFormulario" colspan="2">Lista de Requisitos:</td>
					</tr>
					
					<tr>						
						<td colspan="2">
							<t:dataTable id="dtComponentesProva" value="#{discenteMonitoria.provaSelecao.componentesObrigatorios}" var="compProva" 
									align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" 
									rendered="#{not empty discenteMonitoria.provaSelecao.componentesObrigatorios}">
				
									<t:column>
											<f:facet name="header"><f:verbatim><center>Obrigatório</center></f:verbatim></f:facet>								
											<center><h:outputText value="#{compProva.obrigatorio ? 'SIM':'NÃO'}" /></center>
									</t:column>
									<t:column>
										<f:facet name="header"><f:verbatim>Componente Curricular</f:verbatim></f:facet>
										<h:outputText value="#{compProva.componenteCurricularMonitoria.disciplina.descricao}" />
									</t:column>			
							</t:dataTable>
						</td>
					</tr>
					
					<tr>
						<td class="subFormulario" colspan="2">Dados bancários:</td>
					</tr>
					<tr>
						<td colspan="2">
							<div class="descricaoOperacao">
								<p>
									Para se inscrever na seleção de monitoria verifique se os seus dados bancários estão devidamente atualizados. 
									Abaixo estão as informações localizadas no sistema. Caso estejam incorretas, atualize-as antes de proceder
									a inscrição. 
								</p>
								<p>
									<h:commandLink action="#{ alteracaoDadosDiscente.iniciarAcessoDiscente}" value="Clique aqui "/>
									para efetuar a atualização dos seus dados bancários. 
								</p>
							</div> 
						</td>
					</tr>
					<tr>
						<th><b>Banco:</b></th>
						<td>
							<h:outputText value="#{discenteMonitoria.obj.banco.denominacao}"></h:outputText>
						</td>
					</tr>
					<tr>
						<th><b>Agência:</b></th>
						<td>
							<h:outputText value="#{discenteMonitoria.obj.agencia}"></h:outputText>
						</td>
					</tr>
					<tr>
						<th><b>Conta:</b></th>
						<td>
							<h:outputText value="#{discenteMonitoria.obj.conta}"></h:outputText>
						</td>
					</tr>
					<tr>
						<th><b>Operação:</b></th>
						<td>
							<h:outputText value="#{discenteMonitoria.obj.operacao}"></h:outputText>
						</td>
					</tr>
					
					<tr>
						<td class="subFormulario" colspan="2">Dados do Aluno:</td>
					</tr>
					<tr>
	 					<th class="obrigatorio" ><b>Email:</b></th>
	 					<td><h:inputText id="email_dados_aluno" value="#{discenteMonitoria.dados.email}" /></td>
	 				</tr>
	 				<tr>
	 					<th class="obrigatorio"><b>Telefone:</b></th>
	 					<td><h:inputText id="telefone_dados_aluno" maxlength="8" value="#{discenteMonitoria.dados.telefone}" onkeyup="return formatarInteiro(this);" /></td>
	 				</tr>
	 				<tr>
	 					<th class="obrigatorio"><b>Qualificações:</b></th>
	 					<td><h:inputTextarea id="qualificacoes_dados_aluno" value="#{discenteMonitoria.dados.qualificacoes}" rows="5" style="width: 95%" /></td>
	 				</tr>
	 				<tr>
	 					<th><b>Currículo Lattes:</b></th>
	 					<td><h:inputText id="lattes_dados_aluno" value="#{discenteMonitoria.dados.linkLattes}" style="width: 96%" /></td>
	 				</tr>
			</tbody>

			<tfoot>						
					<tr>
						<td colspan="2">
							<center>
								<input type="hidden" value="${discenteMonitoria.provaSelecao.id}" name="id"/>	
								<h:commandButton value="<< Voltar" action="#{agregadorBolsas.telaBusca}"/>
								<h:commandButton value="Inscrever-se na Seleção" action="#{ discenteMonitoria.realizarInscricaoDiscente }"/>
								<h:commandButton value="Cancelar" action="#{ discenteMonitoria.cancelar }" onclick="#{confirm}"/>
							</center>
						</td>
					</tr>
				</tfoot>
				
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>

	<br/>

	<center>
		<h:form>
			<input type="hidden" value="${discenteMonitoria.provaSelecao.projetoEnsino.id}" name="id"/>	
			<h:commandButton value="Ver mais detalhes deste projeto" action="#{projetoMonitoria.view}"/>
		</h:form>
	</center>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>