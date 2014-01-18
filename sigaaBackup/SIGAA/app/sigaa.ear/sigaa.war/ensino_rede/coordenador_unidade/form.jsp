<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/a4j" prefix="a4j" %>
<style>
	.negrito {font-weight: bold !important;}
</style>
<f:view>
	<h2> <ufrn:subSistema /> > Cadastro de ${coordenadorUnidadeMBean.cadastroSecretaria?" Secretário(a) " :"Coordenador(a) " }da Unidade</h2>
	
	
	<h:form id="form">
	<table class="formulario" style="width:70%;">
		<caption>Dados do(a) ${coordenadorUnidadeMBean.cadastroSecretaria?" Secretário(a) " :"Coordenador(a) " }da Unidade</caption>
		<tbody>
			<tr>
				<th style="width: 20%;"  class="rotulo"> Instituição: </th>
				<td colspan="3">${coordenadorUnidadeMBean.obj.dadosCurso.campus.instituicao.sigla} - ${coordenadorUnidadeMBean.obj.dadosCurso.campus.sigla}</td>
			</tr>
			<tr>
				<th style="width: 20%;" class="rotulo"> Nome: </th>
				<td colspan="3">
					${coordenadorUnidadeMBean.obj.pessoa.nome}
				</td>
			</tr>
			<tr>
				<th ><h:outputText styleClass="#{coordenadorUnidadeMBean.cadastroSecretaria ? 'negrito':'obrigatorio' }" value="Cargo:"/> </th>
				<c:if test="${coordenadorUnidadeMBean.cadastroSecretaria}">
					<td colspan="3">
						<h:outputText value="SECRETÁRIO(A)"/>	
					</td>
				</c:if>
				<c:if test="${not coordenadorUnidadeMBean.cadastroSecretaria}">
					<td colspan="3">
						<h:selectOneMenu value="#{coordenadorUnidadeMBean.obj.cargo.id}" id="cargo" style="width: 40%;" 
						    disabled="#{!coordenadorUnidadeMBean.acessoCoordenadorGeral or coordenadorUnidadeMBean.cadastroSecretaria}" >
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"  />
							<f:selectItems value="#{coordenadorUnidadeMBean.allCargoAcademico}" id="cargoCoordenadorCombo"/>
						</h:selectOneMenu>
					</td>
				</c:if>
			</tr>
			<tr>
				<th class="obrigatorio">E-mail:</th>
				<td colspan="3">
					<h:inputText value="#{coordenadorUnidadeMBean.obj.dados.email}" id="email" size="60" maxlength="80"/>
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<table class="subFormulario" style="border: 1px solid #cacaca" width="100%" >
						<caption>Telefone(s)</caption>
							<tr>
							<th style="width: 180px; text-align: right;">Tipo:</th>
								<td colspan="3">
									<h:selectOneMenu value="#{coordenadorUnidadeMBean.telefone.tipo}" id="tipo" >
									 	<f:selectItem itemLabel="Tel. Fixo" itemValue="1"/>
									 	<f:selectItem itemLabel="Tel. Celular" itemValue="2"/>
									 	<a4j:support event="onchange" reRender="form" />
									 </h:selectOneMenu>
								
								</td>
							</tr>
							<tr>
								<th style="width: 180px; text-align: right;">Telefone:</th>
								<td width="25%" colspan="2">
									(
									<h:inputText value="#{coordenadorUnidadeMBean.telefone.codigoArea}" disabled="#{coordenadorUnidadeMBean.readOnly}"
									maxlength="2" size="2" id="telFixoDDD" onkeyup="return formatarInteiro(this);"  onblur="return formatarInteiro(this);"/>
									)
									 <h:inputText value="#{coordenadorUnidadeMBean.telefone.numero}" disabled="#{coordenadorUnidadeMBean.readOnly}" 
									  maxlength="10" size="10" id="telFixoNumero" onkeypress=" return mascara( this, mtel );" onblur="return mascara( this, mtel );" />	
								</td>
								<td align="left" id="ramal">
									<c:if test="${coordenadorUnidadeMBean.telefone.tipo == 1 }">
										Ramal:	
											<h:inputText value="#{coordenadorUnidadeMBean.telefone.ramal}" disabled="#{coordenadorUnidadeMBean.readOnly}"
											maxlength="5" size="5" id="ramal" onkeyup="return formatarInteiro(this);"  onblur="return formatarInteiro(this);"/>
									</c:if>
								</td>
							</tr>
							<tfoot>
								<tr>
									<td colspan="4">
										<h:commandButton action="#{coordenadorUnidadeMBean.addTelefone}" id="btAdicionar" title="Adicionar" value="Adicionar"/>
									</td>
								</tr>
							</tfoot>
					</table>
				</td>
			</tr>
			<c:if test="${not empty coordenadorUnidadeMBean.obj.dados.telefones }">
			<tr>
				<td colspan="4" class="infoAltRem">
					
					    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Telefone
				</td>
			</tr>				
			<tr>
				<td colspan="4" class="subFormulario">Telefone(s) Adicionado(s):</td>
			</tr>	
			<tr>
				<td colspan="4">
					<t:dataTable var="item" value="#{coordenadorUnidadeMBean.obj.dados.telefones}"  id="telefones" style="width: 100%;"
					 rowClasses="linhaPar, linhaImpar" >
						<tr >						
							<t:column style="text-align: right;width: 20%;">
								<h:outputText  value="Tel. Fixo:" rendered="#{item.tipo == 1 }"/>
								<h:outputText value="Tel. Celular:" rendered="#{item.tipo == 2 }"/>
							</t:column>
							<t:column style="text-align: left;width: 22%;" >(
								<h:outputText value="#{item.codigoArea}"/>
								) &nbsp;
								<h:outputText value="#{item.numero}"/>
							</t:column>
							<t:column style="text-align: left;width: 30%;" >
								<h:outputText  value="Ramal:" rendered="#{item.ramal > 0 }"/>&nbsp;
								<h:outputText value="#{item.ramal}" rendered="#{item.ramal > 0 }"/>
							</t:column>
							<t:column style="text-align:center;width: 5%;">
								<h:commandLink actionListener="#{coordenadorUnidadeMBean.removerTelefone}" id="btRemover" title="Remover Telefone" 
								onclick="if (!confirm('Confirma a remoção desta informação?')) return false;">
									<f:param name="indice" value="#{item.id}"/>
									<h:graphicImage value="/img/delete.gif" />		
								</h:commandLink>
							</t:column>
						</tr>
					</t:dataTable>
				</td>
			</tr>
			</c:if>
			<c:if test="${empty coordenadorUnidadeMBean.obj.dados.telefones }">
			<tr id="listaVazia">
				<td align="center" colspan="4" id="listaVazia"><span style="color: red;" >Nenhum telefone foi adicionado.</span></td>
			</tr>
			</c:if>
		</tbody>
		<tfoot>
			<tr>	
				<td colspan="4">
					<h:commandButton value="#{coordenadorUnidadeMBean.confirmButton }" id="Cadastrar" action="#{ coordenadorUnidadeMBean.cadastrar }"/>
					<h:commandButton value="Cancelar" id="Cancelar" immediate="true" action="#{ coordenadorUnidadeMBean.voltar }" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
	</table>

	</h:form>
</f:view>

	<br>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	</center>

<script>
/* Máscaras ER */  
function mascara(o,f){  
    v_obj=o  
    v_fun=f  
    setTimeout("execmascara()",1)  
}  
function execmascara(){  
    v_obj.value=v_fun(v_obj.value)  
}  
function mtel(v){  
    v=v.replace(/\D/g,"");             //Remove tudo o que não é dígito  
    v=v.replace(/(\d)(\d{4})$/,"$1-$2");    //Coloca hífen entre o quarto e o quinto dígitos  
    return v;  
}  
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>