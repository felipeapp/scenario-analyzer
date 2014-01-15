<%@include file="/public/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<style>
	.descricaoOperacao{font-size: 1.2em;}
	h3, h4 {	font-variant: small-caps;text-align: center;	margin: 2px 0 20px;}	
	h4 { margin: 15px 0 20px; }
	.descricaoOperacao p { text-align: justify; } 
	.vermelho{color: #FF1111 !important;}
</style>
<f:view>
	<h:outputText value="#{solRevalidacaoDiploma.create}" />
	
	<h:form id="form">
	
			<h2> Revalidaçao de Diplomas </h2>
			<div class="descricaoOperacao">		
				<p>O formulário abaixo deve se utilizado somente pelos inscritos no  
					<h:outputText value="#{solRevalidacaoDiploma.obj.editalRevalidacaoDiploma.titulo}"/> que também desejam:
				</p>
				<ul>
					<li> Reimprimir o comprovante ou ficha de inscrição ;ou</li>
					<li> Esteja no período de reagendamento e deseja selecionar data e horário para entrega dos documentos.</li>
				</ul>
				<c:if test="${solRevalidacaoDiploma.abertoInscricao}">
					<h4> 
						<h:commandLink styleClass="vermelho" id="linkFormularioInscricao" action="#{solRevalidacaoDiploma.preCadastrarPublico}">
							Clique Aqui Para se Inscrever para Revalidação de Diploma
							<f:param name="aba" value="p-graduacao" />
						</h:commandLink>
					</h4>
				</c:if>		
			</div>

			<table class="formulario" width="35%" align="center">
				<tbody>
					<caption>Comprovante de Inscrição/Reagendamento</caption>

					<tr>
						<th  align="right" class="required"> Nacionalidade:</th>
						<td align="left">
		 				<a4j:region> 
							<h:selectOneMenu value="#{solRevalidacaoDiploma.filtroNacionalidade}" id="nacionalidade"	>
								<f:selectItems value="#{pais.allCombo}" />
								<a4j:support event="onchange" reRender="documentoBusca" />
							</h:selectOneMenu>&nbsp;
						</a4j:region> 
						</td>
					</tr>
					
					<t:htmlTag value="tbody" id="documentoBusca">
						<tr>
							<c:choose>
								<c:when test="${solRevalidacaoDiploma.nacionalidadePadrao}">
									<th  align="right" class="required">CPF:</th>
									<td align="left">
											<h:inputText value="#{solRevalidacaoDiploma.filtroCpf}"   
											size="14" converter="convertCpf" 
											maxlength="14" id="cpf" onkeypress="formataCPF(this, event, null)"/>
									</td>
								</c:when>
								<c:otherwise>
										<th align="right" class="required">Nº Passaporte:</th>
										<td align="left">
					 							<h:inputText value="#{solRevalidacaoDiploma.filtroPassaporte}"  
					 							size="14" maxlength="25" id="passaporte"  />
				 						</td>								
								</c:otherwise>
							</c:choose>
						</tr>
					</t:htmlTag>
					<tfoot>
					<tr>
						<td colspan="2" align="center">
						<h:commandButton action="#{solRevalidacaoDiploma.visualizarAgenda}" value="Visualizar"/>&nbsp;
						<h:commandButton action="#{solRevalidacaoDiploma.cancelarPublic}" 	onclick="#{confirm}"  value="Cancelar"/>
						</td>
					</tr>
					</tfoot>
			</table>

		
	</h:form>
	<br>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" />
		 <span class="fontePequena">Campos de preenchimento obrigatório. </span> 
		<br>
		<br>
	</center>

</f:view>

<%@include file="/public/include/rodape.jsp"%>