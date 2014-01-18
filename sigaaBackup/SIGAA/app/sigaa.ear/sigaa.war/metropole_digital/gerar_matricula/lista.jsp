<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/matricula.js"></script>


<script type="text/javascript">

	var checkflag = "false";

	function selectAllCheckBox() {
	    var div = document.getElementById('form');
	    var e = div.getElementsByTagName("input");
	   
	    var i;
	
	    if (checkflag == "false") {
	            for ( i = 0; i < e.length ; i++) {
	                    if (e[i].type == "checkbox"){ e[i].checked = true; }
	            }
	            checkflag = "true";
	    } else {
	            for ( i = 0; i < e.length ; i++) {
	                    if (e[i].type == "checkbox"){ e[i].checked = false; }
	            }
	            checkflag = "false";
	    }
	}

</script>

<script type="text/javascript">
function mascara(o,f){
    obj = o
    fun = f
    setTimeout("gerarmascara()",1)
}

function gerarmascara(){
    obj.value=fun(obj.value)
}

function masknumeros(texto){
    texto = texto.replace(/\D/g,"")
    return texto
}

</script>


<f:view>
<a4j:keepAlive beanName="gerarMatriculaDiscentesIMD"/>
<h2> <ufrn:subSistema /> > Gerar Matrícula dos Discentes sem Matrícula</h2>
	
<h:form id="form">
	<table class="formulario" style="width: 60%">
	  <caption>Informe os critérios da busca</caption>
 		<tbody>
			<tr>
				<th width="25%" class="obrigatorio">Processo Seletivo:</th>
				<td>
					<h:selectOneMenu value="#{gerarMatriculaDiscentesIMD.idProcessoSeletivo}" id="curso" required="true">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{gerarMatriculaDiscentesIMD.processosCombo}" />  
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th>Opção - Pólo - Grupo:</th>
				<td>
					<h:selectOneMenu value="#{gerarMatriculaDiscentesIMD.idOpcao}" id="opcao" required="true">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{gerarMatriculaDiscentesIMD.opcoesCombo}" />   
					</h:selectOneMenu>
				</td>
			</tr>
	    </tbody>
	    <tfoot>
		    <tr>
				<td colspan="2">
					<h:commandButton value="Buscar" action="#{gerarMatriculaDiscentesIMD.buscarDiscentes}" id="listar" />
					<h:commandButton value="Cancelar" action="#{gerarMatriculaDiscentesIMD.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		    </tr>
		</tfoot>
	</table>
	
	<br /><br />
<c:if test="${not empty gerarMatriculaDiscentesIMD.listagemDiscentes}">

	
	
	<rich:dataTable value="#{ gerarMatriculaDiscentesIMD.listagemDiscentes }" styleClass="listagem" rowClasses="linhaPar, linhaImpar" var="discente" width="100%" rowKeyVar="c">
	
		<f:facet name="caption"><f:verbatim>Discentes Encontrados (${fn:length(gerarMatriculaDiscentesIMD.listagemDiscentes)})</f:verbatim></f:facet>

		<rich:column>
			<f:facet name="header">
				<f:verbatim>
				<a href="#" onclick="selectAllCheckBox();">Todos</a>
				</f:verbatim>
			</f:facet>
			<h:selectBooleanCheckbox value="#{ discente.selecionado }"/>
		</rich:column>
		
		
	
		<rich:column>
			<f:facet name="header"><f:verbatim>CPF</f:verbatim></f:facet>
			<h:outputText value="#{discente.pessoa.cpf_cnpjString}"/>
		</rich:column>
		<rich:column>
			<f:facet name="header"><f:verbatim>Discente</f:verbatim></f:facet>
			<h:outputText value="#{discente.pessoa.nome}"/>
		</rich:column>
		<rich:column>
			<f:facet name="header"><f:verbatim>Opção - Pólo - Grupo</f:verbatim></f:facet>
			<h:outputText value="#{discente.opcaoPoloGrupo.descricao}"/>
		</rich:column>
		
		
		<f:facet name="footer">
		<rich:columnGroup>
			<rich:column style="text-align: center" colspan="6">
				<h:commandButton value="Selecionar Discentes" action="#{ gerarMatriculaDiscentesIMD.submeterDiscentes }" />
			</rich:column>
		</rich:columnGroup>
	</f:facet>	
		
		
	
	</rich:dataTable>
	
</c:if>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>