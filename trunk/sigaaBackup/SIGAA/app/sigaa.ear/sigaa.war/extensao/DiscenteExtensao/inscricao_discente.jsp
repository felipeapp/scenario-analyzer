<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao"%>

<c:set var="PROJETO" 				value="<%= String.valueOf(TipoAtividadeExtensao.PROJETO) %>" 			scope="application"/>
<c:set var="PROGRAMA" 				value="<%= String.valueOf(TipoAtividadeExtensao.PROGRAMA) %>" 			scope="application"/>
<c:set var="PRODUTO" 				value="<%= String.valueOf(TipoAtividadeExtensao.PRODUTO) %>" 			scope="application"/>
<c:set var="CURSO" 					value="<%= String.valueOf(TipoAtividadeExtensao.CURSO) %>" 				scope="application"/>
<c:set var="EVENTO"					value="<%= String.valueOf(TipoAtividadeExtensao.EVENTO) %>" 			scope="application"/>
<c:set var="PRESTACAO_SERVICO" 		value="<%= String.valueOf(TipoAtividadeExtensao.PRESTACAO_SERVICO) %>" 	scope="application"/>


<f:view>
	<%@include file="/portais/discente/menu_discente.jsp" %>
	
	<h2><ufrn:subSistema /> > Confirmação de Inscrição para seleção de Ação de Extensão</h2>
	<br>
	
	
	<h:form>
	
		<table class="formulario" width="100%">
			<caption class="listagem"> DADOS DA AÇÃO DE EXTENSÃO </caption>
			
					<%-- DADOS GERAIS, DE TODOS OS TIPOS DE Ação --%>
					<tbody>
					
					<tr>
						<th  width="30%"><b> Código: </b> </th>
						<td> <h:outputText value="#{selecaoDiscenteExtensao.atividade.codigo}"/> </td>
					</tr>
					<tr>
						<th><b> Título da Ação: </b> </th>
						<td> <h:outputText value="#{selecaoDiscenteExtensao.atividade.titulo}"/> </td>
					</tr>
					<tr>
						<th><b> Coordenação: </b> </th>
						<td> <h:outputText value="#{selecaoDiscenteExtensao.atividade.coordenacao.pessoa.nome}"/> </td>
					</tr>					
					<tr>
						<th ><b> Tipo da Ação: </b></th>
						<td> ${selecaoDiscenteExtensao.atividade.tipoAtividadeExtensao} </td>
					</tr>
					<c:if test="${(selecaoDiscenteExtensao.atividade.tipoAtividadeExtensao.id != PROGRAMA) and (selecaoDiscenteExtensao.atividade.tipoAtividadeExtensao.id != PRODUTO)}"> 
						
						<tr>
							<th><b> Município de Realização: </b></th>
							<td> <h:outputText value="#{selecaoDiscenteExtensao.atividade.localRealizacao.municipioString}"/> </td>
						</tr>
		
						<tr>
							<th><b> Espaço de Realização: </b></th>
							<td> <h:outputText value="#{selecaoDiscenteExtensao.atividade.localRealizacao.descricao}"/> </td>
						</tr>
						
					</c:if>
					<tr>
						<th><b> Unidade Proponente:</b> </th>
						<td> <h:outputText value="#{selecaoDiscenteExtensao.atividade.unidade.nome}"/> </td>
					</tr>
				
					<tr>
						<th><b> Outras Unidades Envolvidas:</b> </th>
						<td> 
							<t:dataTable id="unidadesEnvolvidas" value="#{selecaoDiscenteExtensao.atividade.unidadesProponentes}" var="atividadeUnidade" rendered="#{not empty selecaoDiscenteExtensao.atividade.unidadesProponentes}">
				
								<t:column>
									<h:outputText value="#{atividadeUnidade.unidade.nome}"/>
									<f:verbatim> / </f:verbatim>
									<h:outputText value="#{atividadeUnidade.unidade.gestora.sigla}"/>
								</t:column>
				
							</t:dataTable>
						</td>
					</tr>
					
					<tr>
						<th><b> Área Temática Principal: </b> </th>
						<td> <h:outputText value="#{selecaoDiscenteExtensao.atividade.areaTematicaPrincipal.descricao}"/> </td>
					</tr>
					<tr>
						<th><b> Área do CNPq:</b> </th>
						<td> <h:outputText value="#{selecaoDiscenteExtensao.atividade.areaConhecimentoCnpq.nome}"/> </td>
					</tr>
					<tr>
						<th><b>Fonte de Financiamento:</b></th>
						<td>
							<h:outputText value="#{selecaoDiscenteExtensao.atividade.fonteFinanciamentoString}"/>
						</td>
					</tr>
					<tr>
						<th><b> Tipo de Cadastro: </b> </th>
						<td> <h:outputText value="#{selecaoDiscenteExtensao.atividade.registro ? 'REGISTRO' : 'SUBMISSÂO DE PROPOSTA'}"/> </td>
					</tr>
					<tr>
						<td colspan="2"><br/></td>
					</tr>
					
					
					
					<table class="formulario" width="100%">
					<caption>Dados do Aluno</caption>
						
			 				<tr>
			 					<th class="obrigatorio"  width="30%">Email:</th>
			 					<td><h:inputText id="email_dados_aluno" value="#{selecaoDiscenteExtensao.dados.email}" /></td>
			 				</tr>
			 				<tr>
			 					<th class="obrigatorio">Telefone:</th>
			 					<td><h:inputText id="telefone_dados_aluno" maxlength="8" value="#{selecaoDiscenteExtensao.dados.telefone}" onkeyup="return formatarInteiro(this);" /></td>
			 				</tr>
			 				<tr>
			 					<th class="obrigatorio">Qualificações:</th>
			 					<td><h:inputTextarea id="qualificacoes_dados_aluno" value="#{selecaoDiscenteExtensao.dados.qualificacoes}" rows="5" style="width: 95%" /></td>
			 				</tr>
			 				<tr>
			 					<th>Currículo Lattes:</th>
			 					<td><h:inputText id="lattes_dados_aluno" value="#{selecaoDiscenteExtensao.dados.linkLattes}" style="width: 96%" /></td>
			 				</tr>
		 				
					
					</tbody>
					<tfoot>					
						<tr>
							<td  colspan="3">
								<input type="hidden" value="${selecaoDiscenteExtensao.atividade.id}" name="id"/>	
								<h:commandButton value="Registrar-se como Interessado" action="#{selecaoDiscenteExtensao.realizarInscricaoDiscente}"/>
								<h:commandButton value="Mais detalhes desta Ação" action="#{atividadeExtensao.view}"/>
								<h:commandButton value="Cancelar" action="#{selecaoDiscenteExtensao.iniciarInscricaoDiscente}" onclick="#{confirm}"/>
							</td>
						</tr>
					</tfoot>
			</table>
		</table>
			
		
	</h:form>
	<br/>
		<center><h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/>
				<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		</center>
	<br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>