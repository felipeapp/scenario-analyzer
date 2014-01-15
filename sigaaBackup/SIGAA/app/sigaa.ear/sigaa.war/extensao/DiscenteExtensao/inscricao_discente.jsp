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
	
	<h2><ufrn:subSistema /> > Confirma��o de Inscri��o para sele��o de A��o de Extens�o</h2>
	<br>
	
	
	<h:form>
	
		<table class="formulario" width="100%">
			<caption class="listagem"> DADOS DA A��O DE EXTENS�O </caption>
			
					<%-- DADOS GERAIS, DE TODOS OS TIPOS DE A��o --%>
					<tbody>
					
					<tr>
						<th  width="30%"><b> C�digo: </b> </th>
						<td> <h:outputText value="#{selecaoDiscenteExtensao.atividade.codigo}"/> </td>
					</tr>
					<tr>
						<th><b> T�tulo da A��o: </b> </th>
						<td> <h:outputText value="#{selecaoDiscenteExtensao.atividade.titulo}"/> </td>
					</tr>
					<tr>
						<th><b> Coordena��o: </b> </th>
						<td> <h:outputText value="#{selecaoDiscenteExtensao.atividade.coordenacao.pessoa.nome}"/> </td>
					</tr>					
					<tr>
						<th ><b> Tipo da A��o: </b></th>
						<td> ${selecaoDiscenteExtensao.atividade.tipoAtividadeExtensao} </td>
					</tr>
					<c:if test="${(selecaoDiscenteExtensao.atividade.tipoAtividadeExtensao.id != PROGRAMA) and (selecaoDiscenteExtensao.atividade.tipoAtividadeExtensao.id != PRODUTO)}"> 
						
						<tr>
							<th><b> Munic�pio de Realiza��o: </b></th>
							<td> <h:outputText value="#{selecaoDiscenteExtensao.atividade.localRealizacao.municipioString}"/> </td>
						</tr>
		
						<tr>
							<th><b> Espa�o de Realiza��o: </b></th>
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
						<th><b> �rea Tem�tica Principal: </b> </th>
						<td> <h:outputText value="#{selecaoDiscenteExtensao.atividade.areaTematicaPrincipal.descricao}"/> </td>
					</tr>
					<tr>
						<th><b> �rea do CNPq:</b> </th>
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
						<td> <h:outputText value="#{selecaoDiscenteExtensao.atividade.registro ? 'REGISTRO' : 'SUBMISS�O DE PROPOSTA'}"/> </td>
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
			 					<th class="obrigatorio">Qualifica��es:</th>
			 					<td><h:inputTextarea id="qualificacoes_dados_aluno" value="#{selecaoDiscenteExtensao.dados.qualificacoes}" rows="5" style="width: 95%" /></td>
			 				</tr>
			 				<tr>
			 					<th>Curr�culo Lattes:</th>
			 					<td><h:inputText id="lattes_dados_aluno" value="#{selecaoDiscenteExtensao.dados.linkLattes}" style="width: 96%" /></td>
			 				</tr>
		 				
					
					</tbody>
					<tfoot>					
						<tr>
							<td  colspan="3">
								<input type="hidden" value="${selecaoDiscenteExtensao.atividade.id}" name="id"/>	
								<h:commandButton value="Registrar-se como Interessado" action="#{selecaoDiscenteExtensao.realizarInscricaoDiscente}"/>
								<h:commandButton value="Mais detalhes desta A��o" action="#{atividadeExtensao.view}"/>
								<h:commandButton value="Cancelar" action="#{selecaoDiscenteExtensao.iniciarInscricaoDiscente}" onclick="#{confirm}"/>
							</td>
						</tr>
					</tfoot>
			</table>
		</table>
			
		
	</h:form>
	<br/>
		<center><h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/>
				<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> 
		</center>
	<br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>