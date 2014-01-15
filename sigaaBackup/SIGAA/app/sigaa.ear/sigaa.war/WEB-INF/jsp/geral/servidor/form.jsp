<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %> 
 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib uri="/tags/sigaa" prefix="sigaa"  %> 
<br>
<h2>
	<html:link action="/verMenuPrincipal">Principal</html:link> >
	<fmt:message key="titulo.${acao}"><fmt:param value="Servidor"/></fmt:message>
</h2>
<hr>
<%@include file="/WEB-INF/jsp/include/mensagem.jsp"%>
<c:if test="${acao == 'remover'}">
	<br>
	<span class="subtitle">
		<fmt:message key="mensagem.confirma.remocao">
			<fmt:param value="do Servidor"></fmt:param>
		</fmt:message>
	</span>
	<br>
	<br>
</c:if>

<style>
.areaDeDados .dados .texto {
  margin-left: 15em;
}
</style>
<html:javascript staticJavascript="false" formName="servidorForm" />

<html:form action="/geral/${acao}Servidor" method="post" focus="matriculaSiape" onsubmit="return validateServidorForm(this);" >
	<html:hidden property="id" />
    <div class="areaDeDados">
	
    <c:if test="${acao == 'alterar' || acao == 'criar'}">
        <h2>Dados do Servidor</h2>

        <div class="dados">
            
			<div class="head">Pessoa:</div>
    	        <div class="texto">
                	<input 	type="text" name="nomePessoa" readonly="readonly" maxlength="100" size="50" id="inputNome" value="${servidor.pessoa.nome}" onmouseover="this.style.cursor='pointer'"
                			onClick="window.open('${pageContext.request.contextPath}/geral/verTelaBuscaPessoa.do', '','width=670,height=350, top=270, left=250, scrollbars' )"/>
                	<html:hidden property="pessoa" value="${servidor.id}" styleId="inputPessoaId"/>
					<span 	class="search" onmouseover="this.style.cursor='pointer'"
							onClick="window.open('${pageContext.request.contextPath}/ensino/graduacao/verTelaBuscaPessoa.do', '','width=670,height=350, top=270, left=250, scrollbars' )">&nbsp;</span>
					<span class="required">&nbsp;</span>
    	            <br>
					<html:errors property="pessoa" />
				</div>	
           	<br/>

            <div class="head">Matrícula SIAPE: </div>
            <div class="texto">
                <html:text name="servidor" property="matriculaSiape" size="15" maxlength=""/>
                <span class="required">&nbsp;</span><br/>
                <html:errors property="matriculaSiape" />
            </div>
            <br/>

           	<div class="head">Data de Ingresso:</div>
    	        <div class="texto">
                	<sigaa:calendar name="servidor" property="dataIngresso" />
    	            <html:errors property="dataIngresso" />
	                <span class="required">&nbsp;</span><br/>
			</div>	
			<br/>
            
            <div class="head">Lotado em Hospital:</div>
            <div class="texto">
            	<html:radio name="servidor" property="lotadoEmHospital" value="true" />Sim
            	<html:radio name="servidor" property="lotadoEmHospital" value="false" />Não
                <html:errors property="lotadoEmHospital" />
            </div>
            <br/>

            <div class="head">CH / Regime de Trabalho: </div>
            <div class="texto">
                <html:text name="servidor" property="cargaHorariaRegimeTrabalho" size="5" maxlength=""/>
                <html:errors property="cargaHorariaRegimeTrabalho" />
            </div>
            <br/>

            <div class="head">Categoria: </div>
            <div class="texto">
                <html:text name="servidor" property="categoria" size="5" maxlength=""/>
                <html:errors property="categoria" />
            </div>
            <br/>

            <div class="head">Nível de Categoria: </div>
            <div class="texto">
                <html:text name="servidor" property="nivelCategoria" size="5" maxlength=""/>
                <span class="required">&nbsp;</span><br/>
            </div>
            <br/>

            <div class="head">Regime de Trabalho:</div>
            <div class="texto">
                <html:select property="tipoRegimeTrabalho" value="${servidor.tipoRegimeTrabalho.id}" >
	                <html:option value="">> Opções</html:option>
	                <html:options collection="tiposRegimeTrabalho" property="id" labelProperty="descricao"/>
                </html:select>
                <span class="required">&nbsp;</span><br/>
                <html:errors property="tipoRegimeTrabalho" />
            </div>
            <br/>
            
            <div class="head">Regime Jurídico:</div>
            <div class="texto">
                <html:select property="tipoRegimeJuridicoServidor" value="${servidor.tipoRegimeJuridicoServidor.id}" >
	                <html:option value="">> Opções</html:option>
	                <html:options collection="tiposRegimeJuridicoServidor" property="id" labelProperty="descricao"/>
                </html:select>
                <span class="required">&nbsp;</span><br/>
                <html:errors property="tipoRegimeJuridicoServidor" />
            </div>
            <br/>
            
            <div class="head">Vínculo Pessoa:</div>
            <div class="texto">
                <html:select property="tipoVinculoPessoa" value="${servidor.tipoVinculoPessoa.id}" >
	                <html:option value="">> Opções</html:option>
	                <html:options collection="tiposVinculoPessoa" property="id" labelProperty="descricao"/>
                </html:select>
                <span class="required">&nbsp;</span><br/>
                <html:errors property="tipoVinculoPessoa" />
            </div>
            <br/>
            
            <div class="head">Classe de Sevidor:</div>
            <div class="texto">
                <html:select property="tipoClasseServidor" value="${servidor.tipoClasseServidor.id}" >
	                <html:option value="">> Opções</html:option>
	                <html:options collection="tiposClasseServidor" property="id" labelProperty="descricao"/>
                </html:select>
                <span class="required">&nbsp;</span><br/>
                <html:errors property="tipoClasseServidor" />
            </div>
            <br/>
            
            <div class="head">Categoria:</div>
            <div class="texto">
                <html:select property="tipoCategoriaServidor" value="${servidor.tipoCategoriaServidor.id}" >
	                <html:option value="">> Opções</html:option>
	                <html:options collection="tiposCategoriaServidor" property="id" labelProperty="descricao"/>
                </html:select>
                <span class="required">&nbsp;</span><br/>
                <html:errors property="tipoCategoriaServidor" />
            </div>
            <br/>
            
        </div>
	</c:if>
	
    <c:if test="${acao == 'remover'}">
        <h2>Dados do Servidor</h2>
        <div class="dados">

            <div class="head">Matrícula SIAPE:</div> 
            <div class="texto">${servidor.siape}</div>
            <br/>

			<div class="head">Data de Ingresso:</div>
	        <div class="texto"><fmt:formatDate value="${servidor.dataIngresso}" pattern="dd/MM/yyyy"/></div>
    	    <br/>

            <div class="head">Lotado em Hospital:</div> 
            <div class="texto"><fmt:message key="label.${servidor.lotadoEmHospital}" /></div>
            <br/>

            <div class="head">CH / Regime de Trabalho:</div> 
            <div class="texto">${servidor.cargaHorariaRegimeTrabalho}</div>
            <br/>

            <div class="head">Categoria:</div> 
            <div class="texto">${servidor.categoria}</div>
            <br/>

            <div class="head">Nível de Categoria:</div> 
            <div class="texto">${servidor.nivelCategoria}</div>
            <br/>

            <div class="head">Regime de Trabalho:</div> 
            <div class="texto">${servidor.tipoRegimeTrabalho.descricao}</div>
            <br/>

            <div class="head">Regime Jurídico:</div> 
            <div class="texto">${servidor.tipoRegimeJuridicoServidor.descricao}</div>
            <br/>

            <div class="head">Vínculo Pessoa:</div> 
            <div class="texto">${servidor.tipoVinculoPessoa.descricao}</div>
            <br/>

            <div class="head">Classe de Servidor:</div> 
            <div class="texto">${servidor.tipoClasseServidor.descricao}</div>
            <br/>

            <div class="head">Categoria de Servidor:</div> 
            <div class="texto">${servidor.tipoCategoriaServidor.descricao}</div>
            <br/>

	    </div>
    </c:if>
            <div class="botoes">
                <html:submit><fmt:message key="botao.${acao}" /></html:submit>
                <c:choose>
                	<c:when test="${acao == 'criar'}">
		                <html:button property="" onclick="javascript:location.href='${pageContext.request.contextPath}/verMenuPrincipal.do'"><fmt:message key="botao.cancelar" /></html:button>
                	</c:when>
                	<c:otherwise>
	                	<html:button property="" onclick="javascript:location.href='${pageContext.request.contextPath}/geral/listarServidores.do'"><fmt:message key="botao.cancelar" /></html:button>
                	</c:otherwise>
                </c:choose>
            </div>
	</div>
</html:form>

<center>
<br>
<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>

<br><br>
	<html:link action="/verMenuPrincipal.do">Menu Principal</html:link>
</center>
