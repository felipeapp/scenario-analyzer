<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao"%>

<f:view>
	<%@include file="/portais/discente/menu_discente.jsp" %>
	
	<h2><ufrn:subSistema /> > Confirma��o de Inscri��o para sele��o de A��o Associada</h2>
	<br>
	
	
	<h:form>
	
		<table class="formulario" width="100%">
			<caption class="listagem"> DADOS DA A��O ASSOCIADA </caption>
			
					<tbody>
					
					<tr>
						<th style="font-weight: bold;"> T�tulo: </th>
						<td> <h:outputText value="#{selecaoDiscenteProjeto.projeto.anoTitulo}"/> </td>
					</tr>
					<tr>
						<th style="font-weight: bold;"> Coordena��o: </th>
						<td> <h:outputText  value="#{selecaoDiscenteProjeto.projeto.coordenador.servidor.pessoa.nome}"/> </td>
					</tr>					
					<tr>
						<th style="font-weight: bold;"> Unidade: </th>
						<td> <h:outputText  value="#{selecaoDiscenteProjeto.projeto.unidade.nome}"/> </td>
					</tr>
				
					<tr>
						<th style="font-weight: bold;"> �rea do CNPq: </th>
						<td> <h:outputText  value="#{selecaoDiscenteProjeto.projeto.areaConhecimentoCnpq.nome}"/> </td>
					</tr>
					<tr>
						<th style="font-weight: bold;">Fonte de Financiamento:</th>
						<td>
							<h:outputText  value="#{selecaoDiscenteProjeto.projeto.fonteFinanciamentoString}"/>
						</td>
					</tr>
						<td colspan="2"><br/></td>
					</tr>
					
					
					
					<table class="formulario" width="100%">
					<caption>Dados do Aluno</caption>
						
			 				<tr>
			 					<th class="obrigatorio"  width="30%">Email:</th>
			 					<td><h:inputText id="email_dados_aluno" value="#{selecaoDiscenteProjeto.dados.email}" /></td>
			 				</tr>
			 				<tr>
			 					<th class="obrigatorio">Telefone:</th>
			 					<td><h:inputText id="telefone_dados_aluno" maxlength="10" value="#{selecaoDiscenteProjeto.dados.telefone}" onkeyup="return formatarInteiro(this);" /></td>
			 				</tr>
			 				<tr>
			 					<th class="obrigatorio">Qualifica��es:</th>
			 					<td><h:inputTextarea id="qualificacoes_dados_aluno" value="#{selecaoDiscenteProjeto.dados.qualificacoes}" rows="5" style="width: 95%" /></td>
			 				</tr>
			 				<tr>
			 					<th>Curr�culo Lattes:</th>
			 					<td><h:inputText id="lattes_dados_aluno" value="#{selecaoDiscenteProjeto.dados.linkLattes}" style="width: 96%" /></td>
			 				</tr>
		 				
					
					</tbody>
					<tfoot>					
						<tr>
							<td  colspan="3">
								<input type="hidden" value="${selecaoDiscenteProjeto.projeto.id}" name="id"/>	
								<h:commandButton value="Registrar-se como Interessado" action="#{ selecaoDiscenteProjeto.realizarInscricaoDiscente }"/>
								<h:commandButton value="Mais detalhes desta A��o" action="#{projetoBase.view}"/>
								<h:commandButton value="Cancelar" action="#{selecaoDiscenteProjeto.iniciarInscricaoDiscente}" onclick="#{confirm}"/>
							</td>
						</tr>
					</tfoot>
			</table>		
		</table>
	</h:form>
	<center><h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/>
				<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> 
		</center>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>