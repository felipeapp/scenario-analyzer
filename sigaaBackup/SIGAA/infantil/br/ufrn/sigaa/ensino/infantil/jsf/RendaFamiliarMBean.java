package br.ufrn.sigaa.ensino.infantil.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.infantil.dominio.RendaFamiliar;

/**
 * Gerado pelo CrudBuilder
 * MBean para gerenciar as faixas de renda familiares
 */
@Component
@Scope("request")
public class RendaFamiliarMBean extends SigaaAbstractController<RendaFamiliar> {

    public RendaFamiliarMBean() {
        obj = new RendaFamiliar();
    }

    @Override
    public String getFormPage() {
        return "/infantil/RendaFamiliar/form.jsf";
    }

    @Override
    public String getListPage() {
        return "/infantil/RendaFamiliar/lista.jsf";
    }

    @Override
    public void beforeRemover() throws DAOException {
        setId();
        try {
            prepareMovimento(ArqListaComando.REMOVER);
        } catch (ArqException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<SelectItem> getAllCombo() throws ArqException {
        return toSelectItems(getAll(), "id", "descricao");
    }
}
