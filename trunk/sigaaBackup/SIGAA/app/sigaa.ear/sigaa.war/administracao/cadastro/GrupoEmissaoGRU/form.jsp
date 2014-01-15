<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
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
    v=v.replace(/(\d)(\d{1})$/,"$1-$2");    //Coloca hífen entre o quarto e o quinto dígitos  
    return v;  
}  
</script>

<f:view>
	<h2><ufrn:subSistema /> > Cadastro de Grupo de Emissão de GRU</h2>

	<div class="descricaoOperacao">
		<p>Caro Usuário,</p>
		<p>Informe os valores abaixo para cadastrar um Grupo de Emissão de
			GRU. Em caso de dúvidas, consulte o departamento financeiro da
			instituição para saber quais os valores a informar.</p>
	</div>
	<h:form id="form">
		<table class="formulario" width="75%">
			<caption>Dados do Grupo de Emissão de GRU</caption>
				<tr>
					<th>Tipo de GRU:</th>
					<td>
						<h:selectOneRadio id="tipoGRUInscricao" 
							onchange="submit()" onclick="submit()"
							valueChangeListener="#{ grupoEmissaoGRUMBean.tipoGRUListener }"
							value="#{ grupoEmissaoGRUMBean.gruSimples }"
							disabled="#{ grupoEmissaoGRUMBean.obj.id > 0 }">
							<f:selectItem itemValue="true" itemLabel="Simples" />
							<f:selectItem itemValue="false" itemLabel="Cobrança" />
						</h:selectOneRadio>
					</td>
				</tr>
				<c:choose>
					<c:when test="${ grupoEmissaoGRUMBean.gruSimples }">
						<tr>
							<th class="obrigatorio">Código da Gestão:</th>
							<td><h:inputText
									onkeyup="return formatarInteiro(this);"
									value="#{ grupoEmissaoGRUMBean.obj.codigoGestao }"
									size="5" maxlength="5" id="codigoGestaoGRUInscricao"/>
							</td>
						</tr>
						<tr>
							<th class="obrigatorio">Código da Unidade Gestora:</th>
							<td><h:inputText
									onkeyup="return formatarInteiro(this);"
									value="#{ grupoEmissaoGRUMBean.obj.codigoUnidadeGestora }"
									size="6" maxlength="6" id="codigoUnidadeGestoraGRUInscricao" />
							</td>
						</tr>
					</c:when>
					<c:when test="${ !grupoEmissaoGRUMBean.gruSimples }">
						<tr>
							<th class="obrigatorio">Agência:</th>
							<td><h:inputText
									onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );"
									value="#{ grupoEmissaoGRUMBean.obj.agencia }"
									size="6" maxlength="6" id="agenciaGRUInscricao" />
							</td>
						</tr>
						<tr>
							<th class="obrigatorio">Código Cedente:</th>
							<td><h:inputText
									onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );"
									value="#{ grupoEmissaoGRUMBean.obj.codigoCedente }"
									size="8" maxlength="8" id="codigoCedenteGRUInscricao" />
							</td>
						</tr>
						<tr>
							<th class="obrigatorio">Número do Convênio:</th>
							<td><h:inputText
									onkeyup="return formatarInteiro(this);"
									value="#{ grupoEmissaoGRUMBean.obj.convenio }"
									size="7" maxlength="7" id="convenioGRUInscricao" />
							</td>
						</tr>
					</c:when>
				</c:choose>
				<c:if test="${ grupoEmissaoGRUMBean.obj.id > 0 }">
					<tr>
						<th><h:selectBooleanCheckbox value="#{ grupoEmissaoGRUMBean.obj.ativo }" id="ativo"/></th>
						<td>Ativo</td>
					</tr>
				</c:if>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{grupoEmissaoGRUMBean.confirmButton}" action="#{grupoEmissaoGRUMBean.cadastrar}" id="cadastrar" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{grupoEmissaoGRUMBean.cancelar}" id="cancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br />
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>