<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp" %>
<%@ taglib uri="/tags/a4j" prefix="a4j" %>

	<style>
		.subFormulario {
			color: black !important;
			background-color: white !important;
			border-color: black !important;
		}
	</style>
	<f:view>
	<h:form id="form">
	
	<h2> Registro da Evolução da Criança </h2>
	
	<c:set var="discente" value="#{registroEvolucaoCriancaMBean.obj.discente}"/>
	<c:set var="turma" value="#{registroEvolucaoCriancaMBean.obj.turma}"/>

		<table class="tabelaRelatorio" width="100%" style="border-bottom: 2px solid black;">
		<tr>
			<th width="10%"> Unidade:</th>
			<td colspan="3"> ${turma.disciplina.unidade.nome} </td>
		</tr>
		<tr>
			<th> Turma:</th>
			<td colspan="3"> ${turma.descricaoTurmaInfantil } </td>
		</tr>
		<tr>
			<th>Local:</th>
			<td colspan="3">${turma.local}</td>
		</tr>
		<tr>
			<th> Matrícula:</th>
			<td colspan="3"> ${discente.matricula } </td>
		</tr>
		<tr>
			<th> Discente:</th>
			<td colspan="3"> ${discente.pessoa.nome } </td>
		</tr>
		<tr>
			<th>Status:</th>
			<td> ${discente.statusString } </td>
		</tr>
		</table>
		<br />
	
		<table class="tabelaRelatorio" width="100%" style="border-top: 1px solid black;">
		<tr>
			<td colspan="3">
				Legenda referente as formas de avaliações da turma:
				<ul>
					<a4j:repeat value="#{ formularioEvolucaoCriancaMBean.formasAvaliacaoValidas }" var="formasAva">
							<b> <h:outputText id="formaAvaDescricaoaa" value="#{ formasAva.legenda }" /> </b> -
							<h:outputText id="formaAvaDescricao" value="#{ formasAva.opcoes }" />
							<br/>
					</a4j:repeat>
				</ul>
			 </td>
		</tr>
		
		</table>
		<br />
			
		<table class="formulario" style="border-color: black;background-color:white;" width="100%">
		<caption style="background:url('') !important;color: black;background-color:white;"><h:outputText value="#{ registroEvolucaoCriancaMBean.formulario.periodo }° Bimestre" /></caption>
			
		<a4j:repeat value="#{registroEvolucaoCriancaMBean.itens}" var="itemForm" rowKeyVar="row">
		
					<a4j:region rendered="#{ itemForm.bloco }">
						<tr>
							<td>
								<h:outputText id="labelItemDescricaoBloco" value="#{ itemForm.item.descricao }" style="font-size: 18px; font-weight: bold;" />
							</td>
						</tr>
					</a4j:region>

					<a4j:region rendered="#{ itemForm.area }">
						<tr>
							<td>
								<h:outputText id="labelItemDescricaoArea" value="#{ itemForm.item.descricao }" style="font-size: 16px; font-weight: bold;" />
							</td>
						</tr>
					</a4j:region>
				
 					<a4j:region rendered="#{ itemForm.conteudo }"> 
						<tr>
							<td class="subFormulario">Conteúdo/Objetivos</td>
							<a4j:region rendered="#{ itemForm.item.formaAvaliacao.indefinida }">
								<td  style="text-align: center;" class="subFormulario"></td>
							</a4j:region>
							
							<a4j:region rendered="#{ not itemForm.item.formaAvaliacao.indefinida }">
								<td  style="text-align: center;" class="subFormulario">Avaliar</td>
							</a4j:region>
							
						</tr>
						<tr>
							<td class="subFormulario">
								<h:outputText id="labelItemDescricaoConteudo" value="#{ itemForm.item.descricao }" style="font-size: 14px; padding-left: 10px;"/>
							</td>
							
							<td class="subFormulario" style="text-align: center;">
								<a4j:repeat value="#{itemForm.item.formaAvaliacao.headOpcoes}" var="formaAva">
									<h:outputText value="#{ formaAva }" rendered="#{ itemForm.conteudo && not itemForm.item.formaAvaliacao.indefinida }" style="padding: 20px;"/> 
								</a4j:repeat>
							</td>
						</tr>
					</a4j:region>
					
					<a4j:region rendered="#{ itemForm.subCont }">
							<tr>
								<td class="subFormulario" colspan="2">
									<h:outputText id="labelItemDescricaoSubConteudo" value="#{ itemForm.item.descricao }" style="font-size: 14px; padding-left: 10px;"/>
								</td>
							</tr>
					</a4j:region>
					
					<a4j:region rendered="#{ itemForm.objetivo }">
						<tr>
							<td>
								<h:outputText id="labelItemDescricaoObj" value="#{ itemForm.item.descricao }" style="padding-left: 15px; font-size: 12px;"/>
							</td>
							<td>
								<center>
									<a4j:repeat value="#{itemForm.item.formaAvaliacao.headOpcoes}" var="formaAva">
										<h:outputText value="#{ formaAva }" rendered="#{ itemForm.conteudo && not itemForm.item.formaAvaliacao.indefinida }" style="padding: 20px;"/> 
									</a4j:repeat>
									
									<a4j:repeat value="#{itemForm.item.formaAvaliacao.headOpcoes}" var="formaAva" rowKeyVar="row">
										<h:outputText value="( )"
										rendered="#{ itemForm.objetivo && not itemForm.item.formaAvaliacao.indefinida && itemForm.itemPeriodo.resultado != row }" style="font-weight:bold;padding: 10%;"/> 
										<h:outputText value="(X)"
											rendered="#{ itemForm.objetivo && not itemForm.item.formaAvaliacao.indefinida && itemForm.itemPeriodo.resultado == row }" style="font-weight:bold;padding: 10%;"/> 
									</a4j:repeat>
										
								</center>
							</td>		
						</tr>
					</a4j:region>								
					
					<a4j:region rendered="#{ itemForm.exibirTextArea }">
						<tr>
							<td class="subFormulario" colspan="2"> Observações </td>
						</tr>
						<tr>
							<td colspan="2">
								<h:outputText value="#{ itemForm.itemPeriodo.observacoes }" escape="false"/>
							</td>
						</tr>
					</a4j:region>
		
			</a4j:repeat>
			
		</table>
		
		</h:form>
	
	</f:view>
	



<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp" %>