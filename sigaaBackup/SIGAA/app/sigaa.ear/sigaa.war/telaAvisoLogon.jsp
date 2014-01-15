<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<f:view>
<h:form>

<h2>${ verTelaAvisoLogonMBean.atual.nome }</h2>

${ verTelaAvisoLogonMBean.atual.conteudo }

<div style="text-align: center; margin: 20px auto 10px auto;">
<h:commandButton value="Continuar >>"  action="#{verTelaAvisoLogonMBean.proxima}"/>
</div>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>